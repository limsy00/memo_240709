package com.memo.post;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.bo.PostBO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/post")
public class PostRestController {
	@Autowired
	private PostBO postBO;

	@RequestMapping("/create")
	public Map<String, Object> create(
			@RequestParam("subject") String subject,
			@RequestParam("content") String content, 
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpSession session) { // 파라미터 user 때문에 session 받아야함 
		
		// (2) 글쓴이 번호를 session에서(bo 모르게) 꺼내기 -> db insert 위함
		int userId = (int)session.getAttribute("userId"); // 로그인 풀릴 경우 에러, 로그인 확인 여부와는 다르게 다운캐스팅 -> breakpoint
		String userLoginId = (String)session.getAttribute("userLoginId");
		
		// (3) db insert
		postBO.addPost(userId, userLoginId, subject, content, file);
		
		
		// (1)응답값
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("result", "성공");
		return result;
	}
}
