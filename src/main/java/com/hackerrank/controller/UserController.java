/**
 * 
 */
package com.hackerrank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hackerrank.model.User;
import com.hackerrank.model.UserValidationResponse;
import com.hackerrank.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/userInfo")
@CrossOrigin(origins = "*")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

//    @PostMapping
//    public ResponseEntity<UserValidationResponse> createContact(@RequestBody User user){
//        UserValidationResponse response = userService.createUser(user);

//        if (response.isValid()) {
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserValidationResponse> getUserById(@PathVariable Long id){
//        UserValidationResponse response = userService.getUserById(id);
//
//        if (response.isValid()) {
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    @GetMapping("/")
//    public ResponseEntity<UserValidationResponse> getUserByEmailId(@RequestParam String emailId){
//        UserValidationResponse response = userService.getUserByEmailId(emailId);
//
//        if (response.isValid()) {
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

	@GetMapping("/notifications")
	public ResponseEntity<List<Map<String, String>>> getUserNotifications() {

		List<Map<String, String>> notifications = List.of(
				Map.of("title", "Welcome!", "description", "Hello " + ", welcome to our service."),
				Map.of("title", "Reminder", "description", "Don’t forget to complete your profile."),
				Map.of("title", "Offer", "description", "You have a special discount waiting for you."));

		return ResponseEntity.ok(notifications);
	}

	@PostMapping("/predictions")
	public ResponseEntity<List<Map<String, String>>> getPredictions(@RequestBody User user) {
//        UserValidationResponse response = userService.createUser(user);
		String message = userService.getLLMResponse(user);
		switch (message) {
		case "0":
			message = "You are following a healthy lifstyle";
			break;
		case "1":
			message ="If you work on yoursef and exercise more. you can improve your health";
			break;
		case "2":
			message = "You need to work o yourself. You need to exercise and follow a healthy lifestyle";
			break;
		case "3":
			message = "Your need to work on your lifestyle.You are invery critical situation";
			break;
		default:
			message = "Low risk";

		}
		System.out.println("message" + message);
		List<Map<String, String>> notifications = List.of(
				Map.of("title", "Welcome!", "description", "Hello " + ", welcome to our service."),
				Map.of("title", "Reminder", "description", "Don’t forget to complete your profile."),
				Map.of("title", "Offer", "description", "You have a special discount waiting for you."),
				Map.of("title", "LLM", "description", message));
		return ResponseEntity.ok(notifications);
	}

}
