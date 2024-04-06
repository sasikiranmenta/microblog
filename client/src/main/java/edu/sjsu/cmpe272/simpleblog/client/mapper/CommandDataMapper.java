package edu.sjsu.cmpe272.simpleblog.client.mapper;

import edu.sjsu.cmpe272.simpleblog.client.command.CommandData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface CommandDataMapper {

    @Mapping(target = "startingId", source = "startingId")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "saveAttachment", source = "save")
    CommandData.List mapToListCommandData(String startingId, int count, boolean save);

    @Mapping(target = "message", source = "message")
    @Mapping(target = "fileName", source = "fileName")
    CommandData.Post mapToPostCommandData(String message, String fileName);


    @Mapping(target = "id", source = "id")
    CommandData.Create mapToCreateCommandData(String id);
}
