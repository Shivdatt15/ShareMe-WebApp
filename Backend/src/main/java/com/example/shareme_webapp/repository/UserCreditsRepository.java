package com.example.shareme_webapp.repository;

import com.example.shareme_webapp.document.UserCredits;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCreditsRepository extends MongoRepository<UserCredits,String> {

    Optional<UserCredits> findByClerkId(String clerkId);

}
