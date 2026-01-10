package com.github.wannaphong.jthainlp.tokenize;

import com.github.wannaphong.jthainlp.util.Trie;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for newmm tokenizer.
 */
public class NewmmTokenizerTest {
    
    @Test
    public void testBasicTokenization() {
        List<String> words = Arrays.asList("ไทย", "ใน", "ของ", "และ", "เป็น");
        Trie dict = new Trie(words);
        NewmmTokenizer tokenizer = new NewmmTokenizer(dict);
        
        List<String> tokens = tokenizer.tokenize("ไทยและ");
        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
    }
    
    @Test
    public void testEmptyText() {
        Trie dict = new Trie(Arrays.asList("ไทย"));
        NewmmTokenizer tokenizer = new NewmmTokenizer(dict);
        
        List<String> tokens = tokenizer.tokenize("");
        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }
    
    @Test
    public void testNullText() {
        Trie dict = new Trie(Arrays.asList("ไทย"));
        NewmmTokenizer tokenizer = new NewmmTokenizer(dict);
        
        List<String> tokens = tokenizer.tokenize(null);
        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }
    
    @Test
    public void testWordTokenizeAPI() {
        List<String> tokens = Tokenize.wordTokenize("ไทย");
        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
    }
    
    @Test
    public void testWordTokenizeWithCustomDict() {
        Trie customDict = new Trie(Arrays.asList("ไทย", "และ", "ภาษา"));
        List<String> tokens = Tokenize.wordTokenize("ไทยและภาษา", customDict);
        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
    }
    
    @Test
    public void testWordTokenizeWithEngine() {
        List<String> tokens = Tokenize.wordTokenize("ไทย", null, "newmm");
        assertNotNull(tokens);
        assertTrue(tokens.size() > 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEngine() {
        Tokenize.wordTokenize("ไทย", null, "invalid");
    }
    
    @Test
    public void testKeepWhitespace() {
        Trie dict = new Trie(Arrays.asList("ไทย", "ภาษา"));
        List<String> tokensWithSpace = Tokenize.wordTokenize("ไทย ภาษา", dict, "newmm", true);
        List<String> tokensWithoutSpace = Tokenize.wordTokenize("ไทย ภาษา", dict, "newmm", false);
        
        assertNotNull(tokensWithSpace);
        assertNotNull(tokensWithoutSpace);
        
        // With whitespace should have more or equal tokens
        assertTrue(tokensWithSpace.size() >= tokensWithoutSpace.size());
    }
}
