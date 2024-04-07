package edu.sjsu.cmpe272.simpleblog.server.mapper;

import edu.sjsu.cmpe272.simpleblog.server.dto.UserDTO;
import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "user")
    @Mapping(target = "publicKey", source = "publicKey")
    UserDetails mapToUser(UserDTO.CreateRequest createRequest);

    @Mapping(target = "message", source = "message", defaultValue = "welcome")
    UserDTO.CreatedResponse mapToCreatedResponse(String message);
}
