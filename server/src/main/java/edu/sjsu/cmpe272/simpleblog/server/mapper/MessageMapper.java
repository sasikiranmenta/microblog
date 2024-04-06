package edu.sjsu.cmpe272.simpleblog.server.mapper;

import edu.sjsu.cmpe272.simpleblog.server.dto.MessageDTO;
import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import edu.sjsu.cmpe272.simpleblog.server.service.MessageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface MessageMapper {

    @Mapping(target = "sentOn", source = "request.date")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "message", source = "request.message")
    @Mapping(target = "attachment", source = "request.attachment")
    @Mapping(target = "signature", source = "request.signature")
    Message mapToMessage(MessageDTO.CreateRequest request, UserDetails user);

    @Mapping(target = "messageId", source = "msgId")
    MessageDTO.CreateResponse  mapToCreateResponse(Integer msgId);

    @Mapping(source = "limit", target = "limit")
    @Mapping(source = "next", target = "next")
    MessageService.Pagination mapToPagination(MessageDTO.ListRequest request);

    @Mapping(target = "date", source = "sentOn")
    @Mapping(target = "messageId", source = "msgId")
    @Mapping(target = "author", expression = "java(msg.getAuthor().getId())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "attachment", source = "attachment")
    @Mapping(target = "signature", source = "signature")
    MessageDTO.ListResponse mapToListResponse(Message msg);
}
