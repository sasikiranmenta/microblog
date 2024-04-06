package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.dto.UserDTO;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import edu.sjsu.cmpe272.simpleblog.server.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDetailsRepository userDetailsRepository;

    public String getUserPublicKey(String userId) throws Exception {
        return getUserById(userId).map(UserDetails::getPublicKey).orElseThrow(() -> new Exception());
    }

    public Optional<UserDetails> getUserById(String userId) {
        return userDetailsRepository.findById(userId);
    }

    public void createUserDetails(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);
    }
}
