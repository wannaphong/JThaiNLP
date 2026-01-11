package com.github.wannaphong.jthainlp.tokenize;

import com.github.wannaphong.jthainlp.util.ThaiCharacterCluster;
import com.github.wannaphong.jthainlp.util.Trie;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * New Maximum Matching (newmm) tokenizer.
 * Dictionary-based maximal matching word segmentation,
 * constrained by Thai Character Cluster (TCC) boundaries.
 * 
 * Based on PyThaiNLP's newmm implementation.
 */
public class NewmmTokenizer {
    
    private final Trie dictionary;
    
    // Pattern for non-Thai tokens
    private static final Pattern NON_THAI_PATTERN = Pattern.compile(
        "[-a-zA-Z]+|\\d+([,.]\\d+)*|[ \\t]+|\\r?\\n|[^\\u0E00-\\u0E7F \\t\\r\\n]+"
    );
    
    // Pattern for 2-consonant Thai tokens
    private static final Pattern THAI_TWO_CHARS_PATTERN = Pattern.compile("[ก-ฮ]{1,2}");
    
    /**
     * Creates a newmm tokenizer with the given dictionary.
     * 
     * @param dictionary the dictionary trie for word lookup
     */
    public NewmmTokenizer(Trie dictionary) {
        this.dictionary = dictionary;
    }
    
    /**
     * Tokenizes Thai text into words using the newmm algorithm.
     * 
     * @param text the text to tokenize
     * @return list of word tokens
     */
    public List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        
        return onecut(text);
    }
    
    /**
     * Internal tokenization method implementing the newmm algorithm.
     */
    private List<String> onecut(String text) {
        int textLength = text.length();
        if (textLength == 0) {
            return new ArrayList<>();
        }
        
        Set<Integer> validPositions = ThaiCharacterCluster.getTccPositions(text);
        
        // Build word graph: for each position, find all possible words starting there
        Map<Integer, List<Integer>> graph = new HashMap<>();
        
        for (int i = 0; i < textLength; i++) {
            if (i > 0 && !validPositions.contains(i)) {
                continue; // Can only start words at TCC boundaries
            }
            
            String remaining = text.substring(i);
            List<String> words = dictionary.prefixes(remaining);
            
            if (!words.isEmpty()) {
                List<Integer> edges = new ArrayList<>();
                for (String word : words) {
                    int endPos = i + word.length();
                    if (validPositions.contains(endPos)) {
                        edges.add(endPos);
                    }
                }
                if (!edges.isEmpty()) {
                    graph.put(i, edges);
                }
            }
            
            // Also add edges for non-Thai tokens
            Matcher matcher = NON_THAI_PATTERN.matcher(remaining);
            if (matcher.lookingAt()) {
                int endPos = i + matcher.end();
                graph.computeIfAbsent(i, k -> new ArrayList<>()).add(endPos);
            }
        }
        
        // Find shortest path (fewest words) using BFS
        return findShortestPath(text, graph, validPositions);
    }
    
    /**
     * Find the shortest path (fewest tokens) through the word graph.
     */
    private List<String> findShortestPath(String text, Map<Integer, List<Integer>> graph, 
                                          Set<Integer> validPositions) {
        int textLength = text.length();
        List<String> result = new ArrayList<>();
        
        int pos = 0;
        while (pos < textLength) {
            List<Integer> edges = graph.get(pos);
            
            if (edges != null && !edges.isEmpty()) {
                // Choose the longest match (maximal matching)
                int nextPos = Collections.max(edges);
                result.add(text.substring(pos, nextPos));
                pos = nextPos;
            } else {
                // No dictionary word found, handle unknown token
                Matcher matcher = NON_THAI_PATTERN.matcher(text.substring(pos));
                
                if (matcher.lookingAt()) {
                    int endPos = pos + matcher.end();
                    result.add(text.substring(pos, endPos));
                    pos = endPos;
                } else {
                    // Thai token not in dictionary, find minimum skip
                    int endPos = findMinimumSkip(text, pos, textLength, validPositions);
                    result.add(text.substring(pos, endPos));
                    pos = endPos;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Finds the minimum skip position for non-dictionary Thai words.
     */
    private int findMinimumSkip(String text, int beginPos, int textLength, Set<Integer> validPositions) {
        for (int pos = beginPos + 1; pos < textLength; pos++) {
            if (validPositions.contains(pos)) {
                String prefix = text.substring(pos);
                List<String> words = dictionary.prefixes(prefix);
                
                // Check if there are words longer than 2 Thai characters
                for (String word : words) {
                    int endPos = pos + word.length();
                    if (validPositions.contains(endPos) && !THAI_TWO_CHARS_PATTERN.matcher(word).matches()) {
                        return pos;
                    }
                }
                
                // Check if it's a non-Thai token
                Matcher matcher = NON_THAI_PATTERN.matcher(prefix);
                if (matcher.lookingAt()) {
                    return pos;
                }
            }
        }
        
        return textLength;
    }
}
