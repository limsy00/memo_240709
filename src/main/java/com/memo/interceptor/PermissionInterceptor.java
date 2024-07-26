package com.memo.interceptor;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // spring bean
public class PermissionInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws IOException {
		
		// 요청 url path를 꺼냄
		String uri = request.getRequestURI();
		log.info("[@@@ preHandle] uri:{}", uri);
		
		// 로그인 여부 (request에서 session 꺼냄) - parameterX
		HttpSession session = request.getSession();
		Integer userId = (Integer)session.getAttribute("userId");
		
		// 비로그인 && /post -> 로그인 페이지 이동 + 컨트롤러 수행 방지
		if (userId == null && uri.startsWith("/post")) {
			response.sendRedirect("/user/sign-in-view"); // throws 던지기
			return false; // 원래 요청 주소에 대한 컨트롤러 수행X ★★★
		}
		// 로그인 && /user -> 글목록 페이지 이동 + 컨트롤러 수행 방지
		if (userId != null && uri.startsWith("/user")) {
			response.sendRedirect("/post/post-list-view"); // throws 던지기
			return false; // 원래 요청 주소에 대한 컨트롤러 수행X ★★★
		}
		return true; // 모든 컨드롤러 수행
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView mav) {
		
		// view, model 객체가 있다 = html 해석 전
		log.info("[@@@ postHandle]");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			Exception ex) {
		
		// html 완성된 상태
		log.info("[@@@ afterCompletion]");
	}
}
