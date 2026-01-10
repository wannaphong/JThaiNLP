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
    
    // Maximum graph size before cutoff to avoid exponential time
    private static final int MAX_GRAPH_SIZE = 50;
    
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
        // Graph structure: key is start position, value is list of possible end positions
        Map<Integer, List<Integer>> graph = new HashMap<>();
        
        int graphSize = 0;
        Set<Integer> validPositions = ThaiCharacterCluster.getTccPositions(text);
        
        int textLength = text.length();
        PriorityQueue<Integer> positionQueue = new PriorityQueue<>();
        positionQueue.add(0);
        
        int endPos = 0;
        List<String> result = new ArrayList<>();
        
        while (!positionQueue.isEmpty() && positionQueue.peek() < textLength) {
            int beginPos = positionQueue.poll();
            
            // Find all possible words starting at beginPos
            String remaining = text.substring(beginPos);
            List<String> prefixes = dictionary.prefixes(remaining);
            
            for (String word : prefixes) {
                int endPosCandidate = beginPos + word.length();
                if (validPositions.contains(endPosCandidate)) {
                    graph.computeIfAbsent(beginPos, k -> new ArrayList<>()).add(endPosCandidate);
                    graphSize++;
                    
                    if (!positionQueue.contains(endPosCandidate)) {
                        positionQueue.add(endPosCandidate);
                    }
                    
                    if (graphSize > MAX_GRAPH_SIZE) {
                        break;
                    }
                }
            }
            
            // Check if we have a single unambiguous path
            if (positionQueue.size() == 1) {
                int nextPos = positionQueue.peek();
                List<Integer> path = findPath(graph, endPos, nextPos);
                graphSize = 0;
                
                for (int i = 1; i < path.size(); i++) {
                    int start = path.get(i - 1);
                    int end = path.get(i);
                    result.add(text.substring(start, end));
                    endPos = end;
                }
            } else if (positionQueue.isEmpty()) {
                // No candidate found, handle non-dictionary word
                Matcher matcher = NON_THAI_PATTERN.matcher(text.substring(beginPos));
                
                if (matcher.lookingAt()) {
                    // Non-Thai token
                    endPos = beginPos + matcher.end();
                } else {
                    // Thai token not in dictionary, find minimum skip
                    endPos = findMinimumSkip(text, beginPos, textLength, validPositions);
                }
                
                graph.computeIfAbsent(beginPos, k -> new ArrayList<>()).add(endPos);
                graphSize++;
                result.add(text.substring(beginPos, endPos));
                positionQueue.add(endPos);
            }
        }
        
        return result;
    }
    
    /**
     * Finds a path through the graph from start to goal.
     */
    private List<Integer> findPath(Map<Integer, List<Integer>> graph, int start, int goal) {
        Queue<List<Integer>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));
        
        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int vertex = path.get(path.size() - 1);
            
            List<Integer> neighbors = graph.get(vertex);
            if (neighbors != null) {
                for (int pos : neighbors) {
                    if (pos == goal) {
                        List<Integer> newPath = new ArrayList<>(path);
                        newPath.add(pos);
                        return newPath;
                    } else {
                        List<Integer> newPath = new ArrayList<>(path);
                        newPath.add(pos);
                        queue.add(newPath);
                    }
                }
            }
        }
        
        return Collections.singletonList(start);
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
