package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		return "/images/" + directoryName + "/" + file.getOriginalFilename(); // 아직 구현x ->404
	}
	
	// ●글 수정시
	// 파일 삭제 
	// input:이미지 경로(path)		 output:X
	public void deleteFile(String imagePath) { 
		//E:\임서영\6_spring_project\memo\memo_workspace\images\tjdud_1721210522240/이미지명
		
		// E:\\임서영\\6_spring_project\\memo\\memo_workspace\\images//aaaa_0715185513/이미지명
		// 주소에 겹치는 \images\ 지운다.
		Path path = Paths.get(FILE_UPLOAD_PATH + imagePath.replace("/images/", ""));
		
		// 삭제할 이미지가 존재하는가?
		if (Files.exists(path)) {
			// 1.이미지 삭제
			try {
				Files.delete(path);
			} catch (IOException e) { // @Slf4j 추가 후
				log.info("[FileManagerService 파일삭제] 삭제 실패. path:{}", path.toString());
				return;
			}
			
			// 2.폴더(디렉토리 삭제)
			path = path.getParent();
			
			if (Files.exists(path)) { // 삭제할 폴더가 존재하는가?
				try {
					Files.delete(path);
				} catch (IOException e) {
					log.info("[FileManagerService 파일삭제] 디렉토리 삭제 실패 path:{}", path.toString());
				}

			}	
		}
	}
}
