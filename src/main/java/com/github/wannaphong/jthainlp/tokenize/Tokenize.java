package com.github.wannaphong.jthainlp.tokenize;

import com.github.wannaphong.jthainlp.util.Trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Main tokenization API for JThaiNLP.
 * Provides PyThaiNLP-compatible interface for Thai text tokenization.
 */
public class Tokenize {
    
    private static Trie defaultDictionary = null;
    private static final String DEFAULT_ENGINE = "newmm";
    
    /**
     * Word tokenizer with PyThaiNLP-compatible API.
     * 
     * @param text the text to tokenize
     * @return list of word tokens
     */
    public static List<String> wordTokenize(String text) {
        return wordTokenize(text, null, DEFAULT_ENGINE, true);
    }
    
    /**
     * Word tokenizer with PyThaiNLP-compatible API.
     * 
     * @param text the text to tokenize
     * @param customDict custom dictionary (null to use default)
     * @return list of word tokens
     */
    public static List<String> wordTokenize(String text, Trie customDict) {
        return wordTokenize(text, customDict, DEFAULT_ENGINE, true);
    }
    
    /**
     * Word tokenizer with PyThaiNLP-compatible API.
     * 
     * @param text the text to tokenize
     * @param customDict custom dictionary (null to use default)
     * @param engine tokenization engine ("newmm" is default and currently only supported)
     * @return list of word tokens
     */
    public static List<String> wordTokenize(String text, Trie customDict, String engine) {
        return wordTokenize(text, customDict, engine, true);
    }
    
    /**
     * Word tokenizer with PyThaiNLP-compatible API.
     * 
     * @param text the text to tokenize
     * @param customDict custom dictionary (null to use default)
     * @param engine tokenization engine ("newmm" is default and currently only supported)
     * @param keepWhitespace whether to keep whitespace in output
     * @return list of word tokens
     */
    public static List<String> wordTokenize(String text, Trie customDict, String engine, boolean keepWhitespace) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        
        Trie dict = customDict;
        if (dict == null) {
            dict = getDefaultDictionary();
        }
        
        List<String> tokens;
        
        if ("newmm".equals(engine)) {
            NewmmTokenizer tokenizer = new NewmmTokenizer(dict);
            tokens = tokenizer.tokenize(text);
        } else {
            throw new IllegalArgumentException("Unsupported tokenization engine: " + engine);
        }
        
        // Filter whitespace if requested
        if (!keepWhitespace) {
            List<String> filtered = new ArrayList<>();
            for (String token : tokens) {
                if (!token.trim().isEmpty()) {
                    filtered.add(token);
                }
            }
            return filtered;
        }
        
        return tokens;
    }
    
    /**
     * Gets the default Thai word dictionary.
     * Loads from resources on first access.
     */
    private static synchronized Trie getDefaultDictionary() {
        if (defaultDictionary == null) {
            defaultDictionary = loadDefaultDictionary();
        }
        return defaultDictionary;
    }
    
    /**
     * Loads the default dictionary from resources.
     */
    private static Trie loadDefaultDictionary() {
        List<String> words = new ArrayList<>();
        
        // Try to load from resources
        try (InputStream is = Tokenize.class.getResourceAsStream("/thai_words.txt")) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            words.add(line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load default dictionary: " + e.getMessage());
        }
        
        // Add some basic Thai words if dictionary is empty
        if (words.isEmpty()) {
            words.addAll(getBasicThaiWords());
        }
        
        return new Trie(words);
    }
    
    /**
     * Returns a basic set of common Thai words for fallback.
     */
    private static List<String> getBasicThaiWords() {
        List<String> words = new ArrayList<>();
        // Common Thai words
        words.add("ไทย");
        words.add("ใน");
        words.add("ของ");
        words.add("และ");
        words.add("เป็น");
        words.add("การ");
        words.add("ที่");
        words.add("มี");
        words.add("ได้");
        words.add("จาก");
        words.add("โดย");
        words.add("ซึ่ง");
        words.add("คือ");
        words.add("ให้");
        words.add("แต่");
        words.add("นี้");
        words.add("นั้น");
        words.add("อื่น");
        words.add("มาก");
        words.add("น้อย");
        words.add("ดี");
        words.add("ไม่");
        words.add("ก็");
        words.add("จะ");
        words.add("ถ้า");
        words.add("หรือ");
        words.add("เพราะ");
        words.add("เมื่อ");
        words.add("กับ");
        words.add("ตาม");
        words.add("ถึง");
        words.add("เพื่อ");
        words.add("ว่า");
        words.add("กัน");
        words.add("ขึ้น");
        words.add("ลง");
        words.add("มา");
        words.add("ไป");
        words.add("ออก");
        words.add("เข้า");
        return words;
    }
}
