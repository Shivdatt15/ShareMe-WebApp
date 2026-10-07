package com.example.shareme_webapp.service;

import com.example.shareme_webapp.document.UserCredits;
import com.example.shareme_webapp.repository.UserCreditsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditsService {

    private final UserCreditsRepository userCreditsRepository;


    private final ProfileService profileService;

    public UserCredits createInitialCredits(String clerkId) {

        return userCreditsRepository
                .findByClerkId(clerkId)
                .orElseGet(() ->
                        userCreditsRepository.save(
                                UserCredits.builder()
                                        .clerkId(clerkId)
                                        .credits(10)
                                        .build()
                        )
                );
    }

    public UserCredits getUserCredits(String clerkId) {
        return userCreditsRepository.findByClerkId(clerkId)
                .orElseGet(() -> createInitialCredits(clerkId));
    }

    public UserCredits getUserCredits(){
        String clerkId = profileService.getCurrentProfile().getClerkId();
        return getUserCredits(clerkId);
    }

    public Boolean hasEnoughCredits (int requiredCredits) {
        UserCredits userCredits = getUserCredits();
        return userCredits.getCredits() >= requiredCredits;

    }

    public UserCredits consumeCredit() {
        UserCredits userCredits = getUserCredits();

        if (userCredits.getCredits() <= 0) {
            return null;
        }

        userCredits.setCredits(userCredits.getCredits() - 1);
        return userCreditsRepository.save(userCredits);

    }
}
