package com.vitec.translator.service;

import com.vitec.translator.model.ChatCompletionMessage;
import com.vitec.translator.model.ChatCompletionRequest;
import com.vitec.translator.model.ChatCompletionResponse;
import com.vitec.translator.model.MessageRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Palveluluokka, joka vastaa kielenkääntämisestä OpenAI:n API:n avulla.
 */
@Service
public class TranslationService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final String model;

    /**
     * Konstruktori palveluluokalle.
     *
     * @param apiKey OpenAI API avain
     * @param apiUrl OpenAI API osoite
     * @param model  Käytettävä kielimalli
     */
    public TranslationService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}") String apiUrl,
            @Value("${openai.model:gpt-3.5-turbo}") String model) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    /**
     * Kääntää tekstin lähdekielestä kohdekieleen.
     *
     * @param text         Käännettävä teksti
     * @param sourceLanguage Lähdekieli
     * @param targetLanguage Kohdekieli
     * @param domain       Toimiala (valinnainen, voi olla null)
     * @return Käännetty teksti
     */
    public String translate(String text, String sourceLanguage, String targetLanguage, String domain) {
        // Luodaan tehokas prompt käännöstä varten
        String prompt = createTranslationPrompt(text, sourceLanguage, targetLanguage, domain);
        
        // Luodaan viestit ChatGPT:lle
        ChatCompletionMessage systemMessage = new ChatCompletionMessage(
                MessageRole.SYSTEM.getValue(),
                "You are a professional translator with expertise in multiple languages. " +
                "Your task is to translate the provided text accurately while maintaining the " +
                "original meaning, tone, and context. Respond with only the translated text, " +
                "without any explanations or additional notes."
        );
        
        ChatCompletionMessage userMessage = new ChatCompletionMessage(
                MessageRole.USER.getValue(),
                prompt
        );
        
        // Luodaan API-pyyntö
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);
        request.setMessages(Arrays.asList(systemMessage, userMessage));
        request.setTemperature(0.3); // Alhaisempi lämpötila tarkemmille käännöksille
        
        // Lähetetään API-pyyntö
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ChatCompletionResponse> response = restTemplate.postForEntity(
                apiUrl,
                entity,
                ChatCompletionResponse.class
        );
        
        // Käsitellään vastaus
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ChatCompletionResponse completionResponse = response.getBody();
            if (completionResponse.getChoices() != null && !completionResponse.getChoices().isEmpty()) {
                return completionResponse.getChoices().get(0).getMessage().getContent().trim();
            }
        }
        
        throw new RuntimeException("Käännöksen hakeminen epäonnistui: " + response.getStatusCode());
    }
    
    /**
     * Luo tehokkaan promptin käännöstä varten.
     *
     * @param text         Käännettävä teksti
     * @param sourceLanguage Lähdekieli
     * @param targetLanguage Kohdekieli
     * @param domain       Toimiala (valinnainen)
     * @return Muotoiltu prompt käännöstä varten
     */
    private String createTranslationPrompt(String text, String sourceLanguage, String targetLanguage, String domain) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("Translate the following text from ").append(sourceLanguage)
                    .append(" to ").append(targetLanguage).append(".\n\n");
        
        // Lisätään toimialakohtainen konteksti jos määritelty
        if (domain != null && !domain.isEmpty()) {
            promptBuilder.append("This text is from the domain of ").append(domain)
                        .append(". Please ensure that domain-specific terminology is translated accurately.\n\n");
        }
        
        promptBuilder.append("Text to translate:\n").append(text).append("\n\n");
        promptBuilder.append("Translation:");
        
        return promptBuilder.toString();
    }
    
    /**
     * Kääntää tekstin lähdekielestä kohdekieleen ilman toimialakohtaista kontekstia.
     *
     * @param text         Käännettävä teksti
     * @param sourceLanguage Lähdekieli
     * @param targetLanguage Kohdekieli
     * @return Käännetty teksti
     */
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        return translate(text, sourceLanguage, targetLanguage, null);
    }
    
    /**
     * Tunnistaa kielen ja kääntää tekstin kohdekieleen.
     *
     * @param text          Käännettävä teksti
     * @param targetLanguage Kohdekieli
     * @return Käännetty teksti
     */
    public String translateWithLanguageDetection(String text, String targetLanguage) {
        // Luodaan prompt, joka pyytää ensin tunnistamaan kielen ja sitten kääntämään
        String prompt = "Please identify the language of the following text and then translate it to " + 
                        targetLanguage + ".\n\nText: " + text + "\n\nTranslation:";
        
        // Samankaltainen toteutus kuin translate-metodissa, mutta erilaisella promptilla
        ChatCompletionMessage systemMessage = new ChatCompletionMessage(
                MessageRole.SYSTEM.getValue(),
                "You are a professional translator with expertise in multiple languages. " +
                "First identify the source language, then translate the provided text accurately. " +
                "Respond with only the translated text, without any explanations."
        );
        
        ChatCompletionMessage userMessage = new ChatCompletionMessage(
                MessageRole.USER.getValue(),
                prompt
        );
        
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);
        request.setMessages(Arrays.asList(systemMessage, userMessage));
        request.setTemperature(0.3);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ChatCompletionResponse> response = restTemplate.postForEntity(
                apiUrl,
                entity,
                ChatCompletionResponse.class
        );
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ChatCompletionResponse completionResponse = response.getBody();
            if (completionResponse.getChoices() != null && !completionResponse.getChoices().isEmpty()) {
                return completionResponse.getChoices().get(0).getMessage().getContent().trim();
            }
        }
        
        throw new RuntimeException("Käännöksen hakeminen epäonnistui: " + response.getStatusCode());
    }
}