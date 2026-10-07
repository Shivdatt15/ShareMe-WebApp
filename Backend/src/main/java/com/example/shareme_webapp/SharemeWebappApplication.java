package com.example.shareme_webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharemeWebappApplication {

	public static void main(String[] args) {

        String mongoUri = System.getenv("MONGODB_URI");

        System.out.println("MONGODB_URI exists: " + (mongoUri != null));
        System.out.println("MONGODB_URI length: " +
                (mongoUri != null ? mongoUri.length() : 0));

		SpringApplication.run(SharemeWebappApplication.class, args);
	}

}
