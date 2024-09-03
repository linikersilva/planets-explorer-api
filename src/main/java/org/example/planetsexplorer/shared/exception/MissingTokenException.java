package org.example.planetsexplorer.shared.exception;

public class MissingTokenException extends RuntimeException {

    public MissingTokenException(String message) {
        super(message);
    }
}