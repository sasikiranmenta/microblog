package edu.sjsu.cmpe272.simpleblog.server.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class MessageDTO {

    @Getter
    @Setter
    public static class CreateRequest {
        Date date;
        String author;
        String message;
        String attachment;
        String signature;
    }

    @Getter
    @Setter
    public static class CreateResponse {
        @JsonAlias("message-id")
        int messageId;
    }

    @Getter
    @Setter
    public static class ListRequest {
        @Max(value = 20)
        @Min(value = 1)
        int limit = 10;
        int next;
    }

    @Getter
    @Setter
    public static class ListResponse extends CreateRequest {
        @JsonAlias("message-id")
        String messageId;
    }
}
