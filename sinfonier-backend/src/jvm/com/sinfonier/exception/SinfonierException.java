package com.sinfonier.exception;

public class SinfonierException extends RuntimeException {
    private static final long serialVersionUID = -6896441005430622984L;

    public SinfonierException(String message) {
        super(message);
    }

    public SinfonierException(Throwable cause) {
        super(cause);
    }

    public SinfonierException(String message, Throwable cause) {
        super(message, cause);
    }
}
