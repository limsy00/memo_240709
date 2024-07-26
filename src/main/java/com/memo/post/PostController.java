package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/post")
public class PostController {
	@Autowired
	private PostBO postBO;
	
	@GetMapping("/post-list-view")
	public String postListView(
			@RequestParam(value = "prevId", required = false) Integer prevIdParam,
			@RequestParam(value = "nextId", required = false) Integer nextIdParam,
			HttpSession session, Model model) { 
		// (1) 로그인 여부 확인 -> parameter: session
		Integer userId = (Integer)session.getAttribute("userId"); // 키명 일치해야하고, 받아오므로 UserRestController와 다르게 getAttribute, null일때 오류나지 않도록 Integer 타입으로 변환하기
		if (userId == null) {
			// 비로그인이면 로그인 페이지로 이동 -> 글에 관련되면 전부 로그인 확인부터!!
			return "redirect:/user/sign-in-view";
		} 
		
		// (2) db 조회 - 글 목록
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		int prevId = 0;
		int nextId = 0;
		if (postList.isEmpty() == false) { // 글목록이 비어있지 않을 때, 페이징 정보 setting
			prevId = postList.get(0).getId(); // 첫번째칸 글 번호
			nextId = postList.get(postList.size() - 1).getId(); // 마지막칸 글 번호
			
			//Q. 이전 방향의 끝인가? 그러면 0
			if (postBO.isPrevLastPageByUserId(userId, prevId)) {
				prevId = 0;
			}
			
			// Q. 다음 방향의 끝인가? 그러면 0
			if (postBO.isNextLastPageByUserId(userId, nextId)) {
				nextId = 0;
			}
		}
		
		// (3) 모델에 담기 -> parameter: Model 추가
		model.addAttribute("prevId", prevId);
		model.addAttribute("nextId", nextId);
		model.addAttribute("postList", postList);
		
		return "post/postList";
	}
	
	/**
	 * 글쓰기 화면
	 * @return
	 */
	@GetMapping("/post-create-view")
	public String postCreateView() {
		return "post/postCreate";
	}
	
	/**
	 * 글 상세 화면 
	 * @param postId
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/post-detail-view")
	public String postDetailView( // 로그인이 풀릴 경우 접근 할 수 없음
			@RequestParam("postId") int postId, Model model, HttpSession session) {
		
		// db select - userId(로그인된 사람의 글 맞는지 확인),postId
		int userId = (int)session.getAttribute("userId"); // 로그인된 상태라고 생각하기 때문에 다운캐스팅 ,breakpoint
		Post post = postBO.getPostByPostIdUserId(userId, postId);
		
		// model
		model.addAttribute("post", post);
		
		// 화면 이동
		return "post/postDetail";
	}
}
