package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.exception.NoUserFoundException;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import edu.sjsu.cmpe272.simpleblog.server.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDetailsRepository userDetailsRepository;

    @SneakyThrows
    public String getUserPublicKey(String username) {
        log.info("Fetching public key for user: {}", username);
        return getUserByUsername(username).map(UserDetails::getPublicKey).orElseThrow(() -> new NoUserFoundException(username));
    }

    public Optional<UserDetails> getUserByUsername(String username) {
        log.info("Fetching details for user: {}", username);
        return userDetailsRepository.findById(username);
    }

    public void createUserDetails(UserDetails userDetails) {
        log.info("Creating user: {}", userDetails.getId());
        userDetailsRepository.save(userDetails);
        log.info("Successfully created user: {}", userDetails.getId());
    }

}
