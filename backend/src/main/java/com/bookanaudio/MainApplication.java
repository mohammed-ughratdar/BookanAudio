package com.bookanaudio;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {

	Dotenv env = Dotenv.load();
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
