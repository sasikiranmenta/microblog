package edu.sjsu.cmpe272.simpleblog.client.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


public class CommandData {

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public abstract static class Data {
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Post extends Data {
        String message;
        String fileName;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class List extends Data {
        String startingId;
        int count;
        boolean saveAttachment;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Create extends Data {
        String id;
    }
}


