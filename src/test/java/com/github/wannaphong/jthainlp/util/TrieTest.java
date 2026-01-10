package com.github.wannaphong.jthainlp.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for Trie data structure.
 */
public class TrieTest {
    
    @Test
    public void testAddAndContains() {
        Trie trie = new Trie();
        trie.add("hello");
        trie.add("world");
        
        assertTrue(trie.contains("hello"));
        assertTrue(trie.contains("world"));
        assertFalse(trie.contains("hi"));
    }
    
    @Test
    public void testConstructorWithCollection() {
        Trie trie = new Trie(Arrays.asList("hello", "world"));
        
        assertTrue(trie.contains("hello"));
        assertTrue(trie.contains("world"));
    }
    
    @Test
    public void testPrefixes() {
        Trie trie = new Trie(Arrays.asList("hello", "help", "world"));
        
        List<String> prefixes = trie.prefixes("hello world");
        assertTrue(prefixes.contains("hello"));
        assertFalse(prefixes.contains("world")); // "world" doesn't start at position 0
    }
    
    @Test
    public void testPrefixesOrdering() {
        Trie trie = new Trie(Arrays.asList("h", "he", "hel", "hell", "hello"));
        
        List<String> prefixes = trie.prefixes("hello");
        assertEquals(5, prefixes.size());
        assertTrue(prefixes.contains("h"));
        assertTrue(prefixes.contains("he"));
        assertTrue(prefixes.contains("hel"));
        assertTrue(prefixes.contains("hell"));
        assertTrue(prefixes.contains("hello"));
    }
    
    @Test
    public void testThaiWords() {
        Trie trie = new Trie(Arrays.asList("ไทย", "ใน", "ของ"));
        
        assertTrue(trie.contains("ไทย"));
        assertTrue(trie.contains("ใน"));
        assertTrue(trie.contains("ของ"));
        assertFalse(trie.contains("test"));
    }
    
    @Test
    public void testThaiPrefixes() {
        Trie trie = new Trie(Arrays.asList("ไทย", "ไทยแลนด์"));
        
        List<String> prefixes = trie.prefixes("ไทยแลนด์");
        assertTrue(prefixes.contains("ไทย"));
        assertTrue(prefixes.contains("ไทยแลนด์"));
    }
    
    @Test
    public void testEmptyString() {
        Trie trie = new Trie();
        trie.add("");
        
        assertFalse(trie.contains(""));
        
        List<String> prefixes = trie.prefixes("");
        assertEquals(0, prefixes.size());
    }
    
    @Test
    public void testNullString() {
        Trie trie = new Trie();
        trie.add(null);
        
        assertFalse(trie.contains(null));
        
        List<String> prefixes = trie.prefixes(null);
        assertEquals(0, prefixes.size());
    }
}
