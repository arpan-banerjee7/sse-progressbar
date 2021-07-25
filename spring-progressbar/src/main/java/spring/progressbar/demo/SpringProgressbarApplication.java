package spring.progressbar.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringProgressbarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringProgressbarApplication.class, args);
	}

}
