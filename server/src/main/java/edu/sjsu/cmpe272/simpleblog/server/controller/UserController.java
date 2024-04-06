package edu.sjsu.cmpe272.simpleblog.server.controller;

import edu.sjsu.cmpe272.simpleblog.server.dto.UserDTO;
import edu.sjsu.cmpe272.simpleblog.server.mapper.UserMapper;
import edu.sjsu.cmpe272.simpleblog.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public UserDTO.CreateResponse createUser(@Valid @RequestBody UserDTO.CreateRequest request) {
        userService.createUserDetails(userMapper.mapToUser(request));
        return userMapper.mapToCreateResponse("welcome");
    }

    @RequestMapping(value = "/{username}/public-key", method = RequestMethod.GET)
    public String getUserPublicKey(@PathVariable(value = "username") String username) throws Exception {
        return userService.getUserPublicKey(username);
    }

}
