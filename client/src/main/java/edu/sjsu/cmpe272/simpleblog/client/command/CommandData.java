package edu.sjsu.cmpe272.simpleblog.client.command;

import lombok.Getter;
import lombok.Setter;


public class CommandData {

    @Setter
    @Getter
    public abstract static class Data {
    }

    @Setter
    @Getter
    public static class Post extends Data {
        String message;
        String fileName;
    }

    @Setter
    @Getter
    public static class List extends Data {
        String startingId;
        int count;
        boolean saveAttachment;
    }

    @Getter
    @Setter
    public static class Create extends Data {
        String id;
    }
}


