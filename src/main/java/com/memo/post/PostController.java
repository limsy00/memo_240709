package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/post")
public class PostController {
	@Autowired
	private PostBO postBO;
	
	@GetMapping("/post-list-view")
	public String postListView(HttpSession session, Model model) { 
		// (1) 로그인 여부 확인 -> parameter: session
		Integer userId = (Integer)session.getAttribute("userId"); // 키명 일치해야하고, 받아오므로 UserRestController와 다르게 getAttribute, null일때 오류나지 않도록 Integer 타입으로 변환하기
		if (userId == null) {
			// 비로그인이면 로그인 페이지로 이동 -> 글에 관련되면 전부 로그인 확인부터!!
			return "redirect:/user/sign-in-view";
		} 
		
		// (2) db 조회 - 글 목록
		List<Post> postList = postBO.getPostListByUserId(userId);
		
		// (3) 모델에 담기 -> parameter: Model 추가
		model.addAttribute("postList", postList);
		
		return "post/postList";
	}

}
