package com.mylearning.practical3;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@SpringBootApplication
public class Practical3Application {

	public static void main(String[] args) {
		SpringApplication.run(Practical3Application.class, args);
	}

	@GetMapping("/")
	public String getHelloWorldMessage(){
		return "Hello GMCA Batch 2025";
	}

	@GetMapping("/time")
	public String getTime(){
		return "Current time is " + new Date().toString();
	}
	@GetMapping("/time/{timezone}")
	public String getTime(@PathVariable String timezone){
		timezone = timezone.replace('-', '/');
		System.out.println(timezone);
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// Use Madrid's time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone(timezone));
		return df.format(date);
	}

}
