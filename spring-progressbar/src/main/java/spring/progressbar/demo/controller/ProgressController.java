package spring.progressbar.demo.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import spring.progressbar.demo.service.FileStorageService;

@CrossOrigin
@RestController
public class ProgressController {

	@Autowired
	FileStorageService fileStorageService;

	private Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	@GetMapping("/progress")
	public SseEmitter eventEmitter() throws IOException {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		UUID guid = UUID.randomUUID();
		sseEmitters.put(guid.toString(), sseEmitter);
		sseEmitter.send(SseEmitter.event().name("GUI_ID").data(guid));
		sseEmitter.onCompletion(() -> sseEmitters.remove(guid.toString()));
		sseEmitter.onTimeout(() -> sseEmitters.remove(guid.toString()));
		return sseEmitter;
	}

	@PostMapping("/upload/local")
	public ResponseEntity<String> uploadFileLocal(@RequestParam("file") MultipartFile file,
			@RequestParam("guid") String guid) throws IOException {
		String message = "";
		try {
			fileStorageService.save(file, sseEmitters.get(guid), guid);
			sseEmitters.remove(guid);
			message = "Uploaded the file successfull:" + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			message = "Could not upload the file:" + file.getOriginalFilename() + "!";
			sseEmitters.remove(guid);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}

	}

}
