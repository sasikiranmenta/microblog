package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

public interface MessageService {
    Message createMessage(Message message);

    List<Message> listMessages(Pagination pagination);

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class Pagination {
        int limit;
        int next;
    }
}
