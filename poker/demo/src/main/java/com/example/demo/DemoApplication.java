package com.example.demo;

import com.example.demo.model.Player;
import com.example.demo.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner seed(PlayerRepository repo) {
		return args -> {
			if (repo.count() == 0) {
				repo.save(new Player("Você", new BigDecimal("1000.00")));
			}
		};
	}
}
