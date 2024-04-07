package edu.sjsu.cmpe272.simpleblog.server.exception;

public class NoUserFoundException extends RuntimeException {
    public NoUserFoundException(String username) {
        super("No User record found with username " + username);
    }
}
