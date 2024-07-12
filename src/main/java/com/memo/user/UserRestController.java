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
	
}
