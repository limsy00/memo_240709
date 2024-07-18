package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component 
public class FileManagerService {
	// 실제 업로드가 된 이미지가 저장될 서버의 경로
	//public static final String FILE_UPLOAD_PATH = ""; => clone 내 노트북
	public static final String FILE_UPLOAD_PATH = "E:\\임서영\\6_spring_project\\memo\\memo_workspace\\images/";
	
	// input: MultipartFile
	// output: String(이미지 경로)
	public String uploadFile(MultipartFile file, String loginId) {
		// 폴더(디렉토리) 생성
		// 예:aaaa_0715185513
		String directoryName = loginId + "_" + System.currentTimeMillis(); // ●breakpoint
		
		// E:\\임서영\\6_spring_project\\memo\\memo_workspace\\images/aaaa_0715185513/
		String filePath = FILE_UPLOAD_PATH + directoryName + "/";
		
		// 폴더 생성
		File directory = new File(filePath); // 객체 생성
		if (directory.mkdir() == false) {
			// 폴더 생성 시 실패하면 경로를 null로 리턴
			return null;
		}
		
		// 파일 업로드 
		try {
			byte[] bytes = file.getBytes(); // error -> try catch 로 수정
			// ★★★한글명으로 된 이미지는 업로드 불가하므로 나중에 영문자로 바꾸기
			Path path = Paths.get(filePath + file.getOriginalFilename());
			Files.write(path, bytes); // 여기서 실제 업로드가 진행
			
		} catch (IOException e) {
			e.printStackTrace();
			return null; // 이미지 업로드 실패시 경로 null-파일 잘못 올렸다고 메모가 날라가는게 아쉬우니..
		}
		
		// 파일 업로드가 성공하면 이미지 url path를 리턴
		// 주소는 이렇게 될 것이다.(예언) : was와 images 폴더를 연결해주는 무언가가 필요
		// localhost/images/aaaa_0715185513/sun.png 
		return "/images" + directoryName + "/" + file.getOriginalFilename(); // 아직 구현x ->404
	}
}
