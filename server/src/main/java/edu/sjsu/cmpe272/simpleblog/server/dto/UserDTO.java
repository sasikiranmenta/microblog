package edu.sjsu.cmpe272.simpleblog.server.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

public class UserDTO {

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateRequest {
        @Pattern(regexp = "[a-z0-9]+")
        String user;
        @JsonAlias("public-key")
        String publicKey;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateResponse {
        String message;
    }

}
