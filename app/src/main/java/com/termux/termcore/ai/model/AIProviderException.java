package com.termux.termcore.ai.model;

public class AIProviderException extends Exception {
    public AIProviderException(String message) {
        super(message);
    }

    public AIProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
