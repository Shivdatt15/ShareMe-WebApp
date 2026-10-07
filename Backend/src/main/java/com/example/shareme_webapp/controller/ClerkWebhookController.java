package com.example.shareme_webapp.controller;

import com.example.shareme_webapp.dto.ProfileDto;
import com.example.shareme_webapp.service.ProfileService;
import com.example.shareme_webapp.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class ClerkWebhookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final ProfileService profileService;

    private final UserCreditsService userCreditsService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-timestamp") String svixTimestamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload) {
        try {
            boolean isValid = verifyWebhookSignature(svixId, svixTimestamp, svixSignature, payload);

            if (!isValid) {
                return ResponseEntity.status(HttpStatus. UNAUTHORIZED).body("Invalid webhook signature");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);

            String eventType = rootNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated (rootNode.path( "data"));
                    break;
                case "user.updated":
                    handleUserUpdated (rootNode.path( "data"));
                    break;
                case "user.deleted":
                    handleUserDeleted (rootNode.path("data"));
                    break;
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
        }

    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asString();
        profileService.deleteProfile(clerkId);
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asString();

        String email="";
        JsonNode emailAddresses = data.path( "email_addresses");
        if (emailAddresses.isArray() && !emailAddresses.isEmpty()) {
            email = emailAddresses.get(0).path("email_address").asString();
        }

        String firstName = data.path("first_name").asString( "");
        String lastName= data.path("last_name").asString("");
        String photoUrl = data.path("image_url").asString( "");

        ProfileDto updatedProfile = ProfileDto.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(photoUrl)
                .build();

      updatedProfile=  profileService.updateProfile(updatedProfile);

      if(updatedProfile == null)
      {
          handleUserCreated(data);
      }
    }

    private void handleUserCreated(JsonNode data) {

        String clerkId= data.path("id").asString();

        String email="";
        JsonNode emailAddresses = data.path("email_addresses");
        if (emailAddresses.isArray() && !emailAddresses.isEmpty()) {
            email = emailAddresses.get(0).path("email_address").asString();
        }

        String firstName = data.path("first_name").asString( "");
        String lastName = data.path("last_name").asString("");
        String photoUrl = data.path("image_url").asString( "");

        ProfileDto newProfile = ProfileDto.builder()
                        .clerkId(clerkId)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .photoUrl(photoUrl)
                        .build();

        profileService.createProfile(newProfile);
        //intially assign credits
        userCreditsService.createInitialCredits(clerkId);
    }

    private boolean verifyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {

        return true;
    }
}