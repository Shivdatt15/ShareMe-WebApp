package com.example.shareme_webapp.controller;

import com.example.shareme_webapp.document.UserCredits;
import com.example.shareme_webapp.dto.UserCreditsDto;
import com.example.shareme_webapp.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserCreditsController {

    private final UserCreditsService userCreditsService;


    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits() {
        UserCredits userCredits = userCreditsService.getUserCredits();
        UserCreditsDto response = UserCreditsDto.builder()
                .credits(userCredits.getCredits())
                .build();

        return ResponseEntity.ok(response);
    }
}
