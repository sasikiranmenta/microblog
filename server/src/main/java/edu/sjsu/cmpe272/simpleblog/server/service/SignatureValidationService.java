package edu.sjsu.cmpe272.simpleblog.server.service;

import edu.sjsu.cmpe272.simpleblog.server.model.UserDetails;

public interface SignatureValidationService {
    <T> void validateSignature(T object, UserDetails userDetails);
}
