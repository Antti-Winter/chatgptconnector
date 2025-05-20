package com.vitec.translator;

import com.vitec.translator.service.TranslationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

/**
 * Komentorivisovellus tekoälykääntäjän testaamiseen.
 */
@SpringBootApplication
public class TranslatorApp {

    public static void main(String[] args) {
        SpringApplication.run(TranslatorApp.class, args);
    }

    @Bean
    public CommandLineRunner translatorRunner(TranslationService translationService) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("=================================================");
            System.out.println("  TEKOÄLYPOHJAINEN LUONNOLLISEN KIELEN KÄÄNTÄJÄ");
            System.out.println("=================================================");
            System.out.println();
            System.out.println("Kirjoita 'exit' lopettaaksesi ohjelman suorituksen.");
            
            boolean running = true;
            
            while (running) {
                try {
                    System.out.println("\nValitse toiminto:");
                    System.out.println("1. Käännä teksti (määrittele lähdekieli)");
                    System.out.println("2. Käännä teksti (automaattinen kielen tunnistus)");
                    System.out.println("3. Lopeta");
                    System.out.print("\nValintasi (1-3): ");
                    
                    String choice = scanner.nextLine().trim();
                    
                    switch (choice) {
                        case "1":
                            translateWithDefinedLanguage(scanner, translationService);
                            break;
                        case "2":
                            translateWithLanguageDetection(scanner, translationService);
                            break;
                        case "3":
                        case "exit":
                            running = false;
                            System.out.println("Ohjelman suoritus päättyy.");
                            break;
                        default:
                            System.out.println("Virheellinen valinta. Valitse 1-3.");
                    }
                } catch (Exception e) {
                    System.out.println("Virhe: " + e.getMessage());
                }
            }
        };
    }
    
    /**
     * Käännöstoiminto, jossa käyttäjä määrittelee lähdekielen.
     */
    private void translateWithDefinedLanguage(Scanner scanner, TranslationService translationService) {
        System.out.print("Syötä lähdekieli (esim. suomi, englanti): ");
        String sourceLanguage = scanner.nextLine().trim();
        
        System.out.print("Syötä kohdekieli (esim. suomi, englanti): ");
        String targetLanguage = scanner.nextLine().trim();
        
        System.out.print("Syötä toimiala (valinnainen, paina Enter ohittaaksesi): ");
        String domain = scanner.nextLine().trim();
        if (domain.isEmpty()) {
            domain = null;
        }
        
        System.out.println("\nSyötä käännettävä teksti (tyhjä rivi lopettaa):");
        StringBuilder textBuilder = new StringBuilder();
        String line;
        
        while (!(line = scanner.nextLine()).isEmpty()) {
            textBuilder.append(line).append("\n");
        }
        
        String textToTranslate = textBuilder.toString().trim();
        
        if (!textToTranslate.isEmpty()) {
            System.out.println("\nKäännetään tekstiä...");
            
            long startTime = System.currentTimeMillis();
            String translatedText;
            
            if (domain != null) {
                translatedText = translationService.translate(textToTranslate, sourceLanguage, targetLanguage, domain);
            } else {
                translatedText = translationService.translate(textToTranslate, sourceLanguage, targetLanguage);
            }
            
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            
            System.out.println("\nKäännös valmis (" + String.format("%.2f", seconds) + " s):");
            System.out.println("---------------------------------------------");
            System.out.println(translatedText);
            System.out.println("---------------------------------------------");
        } else {
            System.out.println("Tekstiä ei syötetty. Käännös peruttu.");
        }
    }
    
    /**
     * Käännöstoiminto automaattisella kielen tunnistuksella.
     */
    private void translateWithLanguageDetection(Scanner scanner, TranslationService translationService) {
        System.out.print("Syötä kohdekieli (esim. suomi, englanti): ");
        String targetLanguage = scanner.nextLine().trim();
        
        System.out.println("\nSyötä käännettävä teksti (tyhjä rivi lopettaa):");
        StringBuilder textBuilder = new StringBuilder();
        String line;
        
        while (!(line = scanner.nextLine()).isEmpty()) {
            textBuilder.append(line).append("\n");
        }
        
        String textToTranslate = textBuilder.toString().trim();
        
        if (!textToTranslate.isEmpty()) {
            System.out.println("\nTunnistetaan kieli ja käännetään tekstiä...");
            
            long startTime = System.currentTimeMillis();
            String translatedText = translationService.translateWithLanguageDetection(textToTranslate, targetLanguage);
            long endTime = System.currentTimeMillis();
            
            double seconds = (endTime - startTime) / 1000.0;
            
            System.out.println("\nKäännös valmis (" + String.format("%.2f", seconds) + " s):");
            System.out.println("---------------------------------------------");
            System.out.println(translatedText);
            System.out.println("---------------------------------------------");
        } else {
            System.out.println("Tekstiä ei syötetty. Käännös peruttu.");
        }
    }
}