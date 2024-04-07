package edu.sjsu.cmpe272.simpleblog.server.controller;

import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.MessageSignatureHolder;
import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.ValidateMessageSignature;
import edu.sjsu.cmpe272.simpleblog.server.dto.MessageDTO;
import edu.sjsu.cmpe272.simpleblog.server.exception.NoUserFoundException;
import edu.sjsu.cmpe272.simpleblog.server.mapper.MessageMapper;
import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import edu.sjsu.cmpe272.simpleblog.server.service.MessageService;
import edu.sjsu.cmpe272.simpleblog.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MessagesController {

    private final MessageService messageService;
    private final UserService userService;
    private final MessageMapper messageMapper;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ValidateMessageSignature
    public MessageDTO.CreatedResponse createMessage(@RequestBody @MessageSignatureHolder MessageDTO.CreateRequest request) {

        UserDetails user = userService.getUserByUsername(request.getAuthor()).orElseThrow(() -> new NoUserFoundException(request.getAuthor()));
        Message message = messageService.createMessage(messageMapper.mapToMessage(request, user));
        log.info("Successfully created message with id: {} from user: {}", message.getMsgId(), request.getAuthor());
        return messageMapper.mapToCreatedResponse(message.getMsgId());
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public List<MessageDTO.PaginatedResponse> listMessages(@Valid @RequestBody MessageDTO.PaginatedRequest request) {
        return messageService.listMessages(messageMapper.mapToPagination(request))
                .stream()
                .map(messageMapper::mapToPaginatedResponse)
                .collect(Collectors.toList());
    }
}
