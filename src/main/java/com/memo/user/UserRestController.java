package com.memo.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memo.common.EncryptUtils;
import com.memo.user.bo.UserBO;
import com.memo.user.entity.UserEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserRestController { // API들만 모아논 class
	
	@Autowired
	private UserBO userBO;

	/**
	 * 아이디 중복확인 API
	 * @param loginId
	 * @return
	 */
	@RequestMapping("/is-duplicated-id")
	public Map<String, Object> isDuplicatedId( // (1)작성 후, 여기에 breakpoint 걸고 확인 -> "이미 사용중인 ID입니다." 나오면 성공
			@RequestParam("loginId") String loginId) {
		
		// (2) db 조회
		UserEntity user = userBO.getUserEntityByLoginId(loginId); // user가 null인지 아닌지에 따라 응답값이 달라짐 => if문으로 수정(1-1)
		
		// (1) 응답값
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		//result.put("is_duplicated_id", true);
		// (1-1) 응답값
		if (user != null) {
			result.put("is_duplicated_id", true);
		} else {
			result.put("is_duplicated_id", false);
		}
		return result;
	}
	
	/** (/ + ** + enter)
	 * 회원가입 API
	 * @param loginId
	 * @param password
	 * @param name
	 * @param email
	 * @return
	 */
	@PostMapping("/sign-up")
	public Map<String, Object> signUp(
			@RequestParam("loginId") String loginId,
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("email") String email) { // signUp.html 파일에 name에 있는 것이 파라미터로 옴
		
		// (2)password md5 알고리즘 -> hashing: 복호화 불가 (vs 암호화: 복호화(복구) 가능) ---- 개인 프로젝트는 보안 취약하지 않는 것 검색해서 사용하기
		// aaaa => 74b8733745420d4d33f80c4663dc5e5
		// aaaa => 74b8733745420d4d33f80c4663dc5e5
		String hashedPassword = EncryptUtils.md5(password);
		
		// (3) db insert -> (1-1)
		UserEntity user = userBO.addUser(loginId, hashedPassword, name, email);
		
		// (1) 응답값
		Map<String, Object> result = new HashMap<>();
//		result.put("code", 200);
//		result.put("result", "성공");
//		return result;
		// (1-1)
		if (user != null) {
			result.put("code", 200);
			result.put("result", "성공");
		} else {
			result.put("code", 500);
			result.put("error_message", "회원가입에 실패했습니다.");
		}
		return result;
	}
	
	/**
	 * 로그인 API
	 * @param loginId
	 * @param password
	 * @param request
	 * @return
	 */
	@PostMapping("/sign-in")
	public Map<String, Object> signIn(
		@RequestParam("loginId") String loginId,
		@RequestParam("password") String password,
		HttpServletRequest request) {
			
			// (2) password hashing > 해싱된 결과물이 나한테 돌아옴 (원본 비번을 파라미터로 받음)			
			String haschedPassword = EncryptUtils.md5(password); // breakpoint 걸고 확인
			
			// (3) db 조회 - loginId, 해싱된 비밀번호 => UserEntity
			UserEntity user = userBO.getUserEntityByLoginIdPassword(loginId, haschedPassword); // -> (1) + (4)
			
			/*
			 * // (4) 로그인 처리 if (user != null) { // 성공
			 * 
			 * } else { // 실패
			 * 
			 * }
			 * 
			 * // (1) 응답값 
			 * Map<String, Object> result = new HashMap<>(); 
			 * result.put("code", 200); 
			 * result.put("result", "성공"); 
			 * return result;
			 */
			
			// (1) + (4) 로그인 처리 및 응답값
			Map<String, Object> result = new HashMap<>(); // haschedPassword에 breakpoint 걸고 확인
			if (user != null) { // 성공
				// (5) 세션에 사용자 정보 담기 (사용자 각각 마다) -- session input
				HttpSession session = request.getSession(); // 비어있는 주머니 001
				session.setAttribute("userId", user.getId()); // 내가 지은 변수 이름(userId, userLoginId...)에 사용자 정보(user.getId(), user.getLoginId()..) 담기
				session.setAttribute("userLoginId", user.getLoginId());
				session.setAttribute("userName", user.getName()); 
				
				result.put("code", 200);
				result.put("result", "성공");
			} else { // 실패
				result.put("code", "403");
				result.put("error_message", "존재하지 않는 사용자 입니다.");
			}
			return result;
	}
}
	

