package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import edu.sjsu.cmpe272.simpleblog.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public Message createMessage(Message message) {
        log.info("Creating new message from user: {}", message.getAuthor().getId());
        return messageRepository.save(message);
    }

    public List<Message> listMessages(Pagination pagination) {
        log.info("Fetching messages");
        Pageable pageable = PageRequest.of(0, pagination.getLimit());
        if (pagination.getNext() == -1) {
            return messageRepository.findMessagesOrderByMessageIdDescLimit(pageable).getContent();
        }
        return messageRepository.findMessagesWithMaxIdLessThanOrderByMessageIdDescLimit(pagination.getNext(), pageable).getContent();
    }
}
