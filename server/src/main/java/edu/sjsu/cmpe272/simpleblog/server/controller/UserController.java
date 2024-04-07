package edu.sjsu.cmpe272.simpleblog.server.controller;

import edu.sjsu.cmpe272.simpleblog.server.dto.UserDTO;
import edu.sjsu.cmpe272.simpleblog.server.mapper.UserMapper;
import edu.sjsu.cmpe272.simpleblog.server.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private static final String WELCOME_MSG = "welcome";
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public UserDTO.CreatedResponse createUser(@Valid @RequestBody UserDTO.CreateRequest request) {

        userService.createUserDetails(userMapper.mapToUser(request));
        return userMapper.mapToCreatedResponse(WELCOME_MSG);
    }

    @RequestMapping(value = "/{username}/public-key", method = RequestMethod.GET)
    public String getUserPublicKey(@PathVariable(value = "username") String username) throws Exception {
        return userService.getUserPublicKey(username);
    }

}
