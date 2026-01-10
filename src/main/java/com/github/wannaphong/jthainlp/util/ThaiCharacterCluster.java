package com.github.wannaphong.jthainlp.util;

import java.util.*;

/**
 * Thai Character Cluster (TCC) boundary detection.
 * Thai text should be broken at TCC boundaries for proper word segmentation.
 */
public class ThaiCharacterCluster {
    
    // Thai Unicode ranges
    private static final char THAI_CONSONANT_START = '\u0E01';
    private static final char THAI_CONSONANT_END = '\u0E2E';
    private static final char THAI_DIGIT_START = '\u0E50';
    private static final char THAI_DIGIT_END = '\u0E59';
    
    // Thai tone marks and vowels
    private static final Set<Character> THAI_FOLLOW_CHARS = new HashSet<>(Arrays.asList(
        '\u0E30', '\u0E31', '\u0E32', '\u0E33', '\u0E34', '\u0E35', '\u0E36', '\u0E37',
        '\u0E38', '\u0E39', '\u0E3A', '\u0E40', '\u0E41', '\u0E42', '\u0E43', '\u0E44',
        '\u0E45', '\u0E46', '\u0E47', '\u0E48', '\u0E49', '\u0E4A', '\u0E4B', '\u0E4C',
        '\u0E4D', '\u0E4E'
    ));
    
    /**
     * Checks if a character is a Thai consonant.
     */
    private static boolean isThaiConsonant(char c) {
        return (c >= THAI_CONSONANT_START && c <= THAI_CONSONANT_END);
    }
    
    /**
     * Checks if a character is a Thai following character (vowel, tone mark, etc.).
     */
    private static boolean isThaiFollowChar(char c) {
        return THAI_FOLLOW_CHARS.contains(c);
    }
    
    /**
     * Checks if a character is part of Thai script.
     */
    private static boolean isThaiChar(char c) {
        return (c >= '\u0E00' && c <= '\u0E7F');
    }
    
    /**
     * Returns all valid TCC breaking positions in the text.
     * Position 0 and the end of text are always valid breaking points.
     * 
     * @param text the text to analyze
     * @return set of valid character positions for breaking
     */
    public static Set<Integer> getTccPositions(String text) {
        Set<Integer> positions = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return positions;
        }
        
        positions.add(0);  // Start is always valid
        positions.add(text.length());  // End is always valid
        
        for (int i = 1; i < text.length(); i++) {
            char current = text.charAt(i);
            char previous = text.charAt(i - 1);
            
            // Can break if current char is not a Thai following character
            // or if previous char is not Thai
            if (!isThaiFollowChar(current) || !isThaiChar(previous)) {
                // Additional check: don't break between Thai consonants and following chars
                if (!(isThaiConsonant(previous) && isThaiFollowChar(current))) {
                    positions.add(i);
                }
                // But we should break if current is Thai consonant
                if (isThaiConsonant(current)) {
                    positions.add(i);
                }
            }
        }
        
        return positions;
    }
}
