package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;

import java.util.Optional;

public interface UserService {

    String getUserPublicKey(String userId);

    Optional<UserDetails> getUserByUsername(String userId);

    void createUserDetails(UserDetails userDetails);
}
