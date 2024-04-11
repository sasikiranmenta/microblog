package edu.sjsu.cmpe272.simpleblog.server.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.MessageSignature;
import edu.sjsu.cmpe272.simpleblog.server.annotation.signature.SignatureOrder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MessageDTO {

    @Getter
    @Setter
    @ToString
    public static class CreateRequest {
        @NotNull(message = "Date cannot be empty")
        @SignatureOrder(order = 1)
        String date;

        @NotBlank(message = "Author cannot be empty")
        @SignatureOrder(order = 2)
        String author;

        @NotBlank(message = "Message cannot be empty")
        @SignatureOrder(order = 3)
        String message;

        @SignatureOrder(order = 4, ignoreIfEmpty = true)
        String attachment;

        @NotBlank(message = "Signature cannot be empty")
        @MessageSignature
        String signature;
    }

    @Getter
    @Setter
    public static class CreatedResponse {
        @JsonAlias("message-id")
        int messageId;
    }

    @Getter
    @Setter
    public static class PaginatedRequest {
        @Max(value = 20, message = "Limit cannot be greater than 20")
        @Min(value = 1, message = "Limit cannot be less than 1")
        int limit = 10;

        int next;
    }

    @Getter
    @Setter
    public static class PaginatedResponse extends CreateRequest {
        @JsonAlias("message-id")
        String messageId;
    }
}
