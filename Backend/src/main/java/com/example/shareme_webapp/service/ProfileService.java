package com.example.shareme_webapp.service;

import com.example.shareme_webapp.document.ProfileDocument;
import com.example.shareme_webapp.dto.ProfileDto;
import com.example.shareme_webapp.repository.ProfileRepository;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileDto createProfile(ProfileDto profileDTO) {

        if(profileRepository.existsByClerkId(profileDTO.getClerkId()))
        {
            return updateProfile(profileDTO);
        }

        ProfileDocument profile = ProfileDocument.builder()
                .clerkId(profileDTO.getClerkId())
                .email(profileDTO.getEmail())
                .firstName(profileDTO.getFirstName())
                .lastName(profileDTO.getLastName())
                .photoUrl(profileDTO.getPhotoUrl())
                .credits(10)
                .createdAt(Instant.now())
                .build();

        profile = profileRepository.save(profile);

        return  ProfileDto.builder()
                          .id(profile.getId())
                         .clerkId(profile.getClerkId())
                         .email(profile.getEmail())
                         .firstName(profile.getFirstName())
                         .lastName(profile.getLastName())
                         .photoUrl(profile.getPhotoUrl())
                         .credits(profile.getCredits())
                         .createdAt(profile.getCreatedAt())
                         .build();
    }

    public ProfileDto updateProfile(ProfileDto profileDto)
    {
        ProfileDocument profile = profileRepository.findByClerkId(profileDto.getClerkId());

        //update fields if they are present in db
        if(profile!=null)
        {
            if(profileDto.getEmail()!=null && ! profileDto.getEmail().isEmpty())
            {
                profile.setEmail(profileDto.getEmail());
            }
            if(profileDto.getFirstName()!=null && ! profileDto.getFirstName().isEmpty())
            {
                profile.setFirstName(profileDto.getFirstName());
            }
            if(profileDto.getLastName()!=null && ! profileDto.getLastName().isEmpty())
            {
                profile.setLastName(profileDto.getLastName());
            }
            if(profileDto.getPhotoUrl()!=null && ! profileDto.getPhotoUrl().isEmpty())
            {
                profile.setPhotoUrl(profileDto.getPhotoUrl());
            }

            profileRepository.save(profile);

            return ProfileDto.builder()
                    .id(profile.getId())
                    .clerkId(profile.getClerkId())
                    .email(profile.getEmail())
                    .firstName(profile.getFirstName())
                    .lastName(profile.getLastName())
                    .photoUrl(profile.getPhotoUrl())
                    .credits(profile.getCredits())
                    .createdAt(profile.getCreatedAt())
                    .build();
        }

        return null;
    }

    public boolean existsByClerkId(String clerkId)
    {
       return profileRepository.existsByClerkId(clerkId);
    }

    public void deleteProfile(String clerkId)
    {
        ProfileDocument existingProfile = profileRepository.findByClerkId(clerkId);
        if(existingProfile !=null)
        {
           profileRepository.delete(existingProfile);
        }

    }

    public ProfileDocument getCurrentProfile() {

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new UsernameNotFoundException(
                    "User not authenticated"
            );
        }

        String clerkId = authentication.getName();

        ProfileDocument profile =
                profileRepository.findByClerkId(clerkId);

        if (profile == null) {
            throw new UsernameNotFoundException(
                    "Profile not found for Clerk ID: " + clerkId
            );
        }

        return profile;
    }


}
