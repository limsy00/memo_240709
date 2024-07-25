package com.memo.post.bo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostBO {
	// 로그에 기록 남기기(2가지): @Slf4j 어노테이션 사용하면 아래 코드 작성하지 않아도 돼
	//private Logger log =  LoggerFactory.getLogger(PostBO.class); // import slf4j
	//private Logger log =  LoggerFactory.getLogger(this.getClass()); 

	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	// input: 로그인 된 사람의 userId
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId);
	}
	
	// ●input: userId, postId
	// output: Post(mybatis) or null
	public Post getPostByPostIdUserId(int userId, int postId) {
		return postMapper.selectPostByPostIdUserId(userId, postId);
	}
	
	// input: 파라미터들(5개)
	// output: X
	public void addPost(int userId, String userLoginId, 
			String subject, String content, MultipartFile file) {
		
		String imagePath = null;
		
		if (file != null) {
			// 업로드 할 이미지가 있을 때에만 업로드
			imagePath = fileManagerService.uploadFile(file, userLoginId);
		}
		
		postMapper.insertPost(userId, subject, content, imagePath); // userLoginId db 저장X
	}
	
	// 글 수정
	/* 이미지 업로드 실패할 경우, 기존 이미지는 남겨둔다. */
	// input: 파라미터들(6개)  output: X
	public void updatePostByPostId(
			int userId, String loginId,
			int postId, String subject, String content,
			MultipartFile file) {
		
		// 기존 글 가져오기
		/* 1) 이미지 교체시, 삭제하기 위함
		 * 2) 업데이트 대상이 있는지 확인하기 위함 */
		Post post = postMapper.selectPostByPostIdUserId(userId, postId); // 위에 만들어 뒀던 쿼리 재사용

		if (post == null) {
			log.warn("[글 수정] post is null. userId:{}, postId:{}", userId, postId); // 지금 userId, postId가 단서로 나옴
			return;
		}
		
		// 파일이 있는 경우
		/* 1) 새 이미지 업로드
		 * 2) 1) 성공하면 기존 이미지 삭제 */
		String imagePath = null;
		
		if (file != null) {
			// 새 이미지 업로드
			imagePath = fileManagerService.uploadFile(file, loginId);
			
			// 업로드 성공 시(null 아님) 기존 이미지가 있으면 제거
			if (imagePath != null && post.getImagePath() != null) {
				// 폴더 + 이미지 제거(서버에서)
				fileManagerService.deleteFile(post.getImagePath()); // 기존 이미지는 글 안에 있음으로 실수 주의★
			}
		}
		
		// db update
		postMapper.updatePostByPostId(postId, subject, content, imagePath);
	}
	
	// 글 삭제 := 글 수정과 비슷한 로직
	/* 이미지까지 삭제해야함 */
	// input: postId, userId 		output: X
	public void deletePostByPostIdUserId(int postId, int userId) {
		// 이미지 존재 시 삭제를 위해 -> 기존글 가져오기 
		Post post = postMapper.selectPostByPostIdUserId(userId, postId);  //breakpoint controller와 같이 확인
		if (post == null) {
			log.info("[글 삭제] post is null. userId:{}, postId:{}", userId, postId);
			return;
		}
		
		/* 글은 지워지지 않았는데 이미지만 지워질 수 있음으로 */
		// 1) db 에서 먼저 delete 하고 
		int rowCount = postMapper.deletePostByPostId(postId);
		
		// 2) 부가적으로 이미지가 존재하면 삭제 (+삭제된 행이 1일 때도 확인)
		if (rowCount > 0 && post.getImagePath() != null) {
			fileManagerService.deleteFile(post.getImagePath());
		}
		
	}
}