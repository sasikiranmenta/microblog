package edu.sjsu.cmpe272.simpleblog.server.controller;

import edu.sjsu.cmpe272.simpleblog.server.dto.MessageDTO;
import edu.sjsu.cmpe272.simpleblog.server.mapper.MessageMapper;
import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import edu.sjsu.cmpe272.simpleblog.server.service.MessageService;
import edu.sjsu.cmpe272.simpleblog.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Validated
public class MessagesController {

    private final MessageService messageService;
    private final UserService userService;
    private final MessageMapper messageMapper;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public MessageDTO.CreateResponse createMessage(@RequestBody MessageDTO.CreateRequest request) throws Exception {

        UserDetails user = userService.getUserById(request.getAuthor()).orElseThrow(() -> new Exception("No user found"));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(user.getPublicKey()));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String payload = "";
        payload += request.getDate().toString();
        payload += request.getAuthor();
        payload += request.getMessage();
        if(request.getAttachment() != null) {
            payload+= request.getAttachment();
        }
        payload = payload.replaceAll(" ", "");
        byte[] shaDigest = md.digest(payload.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(shaDigest);
        boolean isValid = signature.verify(Base64.getDecoder().decode(request.getSignature()));

        if(!isValid) {
            throw new Exception("Invalid signature");
        }


        Message message =  messageService.createMessage(messageMapper.mapToMessage(request, user));
        return messageMapper.mapToCreateResponse(message.getMsgId());
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public List<MessageDTO.ListResponse> listMessages(@Valid @RequestBody MessageDTO.ListRequest request) {
        return messageService.listMessages(messageMapper.mapToPagination(request))
                .stream()
                .map(messageMapper::mapToListResponse)
                .collect(Collectors.toList());
    }
}
