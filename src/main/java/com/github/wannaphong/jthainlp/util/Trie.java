package com.github.wannaphong.jthainlp.util;

import java.util.*;

/**
 * Trie data structure for efficient dictionary lookup.
 * Used for finding word prefixes in text during tokenization.
 */
public class Trie {
    private final TrieNode root;
    
    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;
        
        TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }
    
    /**
     * Creates an empty Trie.
     */
    public Trie() {
        root = new TrieNode();
    }
    
    /**
     * Creates a Trie and adds all words from the collection.
     * 
     * @param words collection of words to add to the trie
     */
    public Trie(Collection<String> words) {
        this();
        for (String word : words) {
            add(word);
        }
    }
    
    /**
     * Adds a word to the Trie.
     * 
     * @param word the word to add
     */
    public void add(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }
    
    /**
     * Checks if the Trie contains a word.
     * 
     * @param word the word to check
     * @return true if the word exists in the trie
     */
    public boolean contains(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return false;
            }
        }
        return current.isEndOfWord;
    }
    
    /**
     * Returns all prefixes of the given text that exist in the Trie.
     * This is used to find all possible word matches starting from the beginning of the text.
     * 
     * @param text the text to find prefixes for
     * @return list of all prefixes that are words in the trie
     */
    public List<String> prefixes(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        
        TrieNode current = root;
        StringBuilder prefix = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                break;
            }
            prefix.append(c);
            if (current.isEndOfWord) {
                result.add(prefix.toString());
            }
        }
        
        return result;
    }
}
