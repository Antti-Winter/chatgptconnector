package com.vitec.translator.model;

/**
 * Enum-luokka viestiroolien määrittelyyn OpenAI API -kommunikaatiossa.
 */
public enum MessageRole {
    
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");
    
    private final String value;
    
    MessageRole(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}