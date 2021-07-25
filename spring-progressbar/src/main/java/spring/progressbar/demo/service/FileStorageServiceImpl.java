package spring.progressbar.demo.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class FileStorageServiceImpl implements FileStorageService {
	private final Path root = Paths.get("uploads/test_");

	@Override
	public void save(MultipartFile file, SseEmitter sseEmitter, String guid) {
		try {
			// Counts total number of lines
			InputStream inputStreamForAll = file.getInputStream();
			InputStream inputStream = file.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamForAll, "UTF-8"));
			BufferedReader readerAll = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			int totalLines = 0;
			while (readerAll.readLine() != null) {
				totalLines++;
			}
			System.out.println(totalLines);
			String line = reader.readLine();

			// read all lines from file
			int lineNumber = 1;
			int previousPercent = -1;
			while (line != null) {
				int uploadPercentage = (lineNumber * 100 / totalLines);
				// compute your percentage
				if (uploadPercentage != previousPercent && uploadPercentage % 5 == 0 && uploadPercentage != 55
						&& uploadPercentage < 100) {
					// Output if different from last time.
					System.out.println(uploadPercentage + "% read");
					Thread.sleep(2000);
					sseEmitter.send(SseEmitter.event().name(guid).data(uploadPercentage));
				}
				// Update the percentage
				previousPercent = uploadPercentage;
				line = reader.readLine();
				lineNumber++;

			}

			// Upload the file after parsing
			byte[] bytes = file.getBytes();
			Path path = Paths.get(root + file.getOriginalFilename());
			Files.write(path, bytes);
			System.out.println("100" + "% read.");
			sseEmitter.send(SseEmitter.event().name(guid).data(100));
		} catch (Exception e) {
			sseEmitter.completeWithError(e);
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		} 

	}
}
