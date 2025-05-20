package com.vitec.translator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Määrittelee keskusteluviestin OpenAI API -kutsuja varten.
 */
public class ChatCompletionMessage {
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("content")
    private String content;
    
    public ChatCompletionMessage() {
    }
    
    public ChatCompletionMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}