package edu.sjsu.cmpe272.simpleblog.server.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
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
        @Pattern(regexp = "[a-z0-9]+", message = "User ID should contain only lowercase alphabets and numerics")
        @NotBlank(message = "User ID cannot be blank")
        String user;

        @JsonAlias("public-key")
        @NotBlank(message = "Public Key cannot be empty")
        String publicKey;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreatedResponse {
        String message;
    }

}
