package edu.sjsu.cmpe272.simpleblog.client.command;

import java.io.IOException;

public interface ClientCommand<T extends CommandData.Data> {
    CommandType getCommandType();

    void executeCommand(T data) throws IOException;
}
