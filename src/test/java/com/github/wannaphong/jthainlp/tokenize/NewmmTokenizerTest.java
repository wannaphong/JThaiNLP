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
    
    /**
     * Comprehensive newmm tests from PyThaiNLP to ensure compatibility.
     */
    @Test
    public void testNewmmComprehensive() {
        // Test null and empty inputs
        List<String> nullResult = Tokenize.wordTokenize(null, null, "newmm", false);
        assertEquals(Arrays.asList(), nullResult);
        
        List<String> emptyResult = Tokenize.wordTokenize("", null, "newmm", false);
        assertEquals(Arrays.asList(), emptyResult);
        
        // Test basic Thai text tokenization
        List<String> result1 = Tokenize.wordTokenize("ฉันรักภาษาไทยเพราะฉันเป็นคนไทย", null, "newmm", false);
        assertEquals(Arrays.asList("ฉัน", "รัก", "ภาษาไทย", "เพราะ", "ฉัน", "เป็น", "คนไทย"), result1);
        
        // Test numeric patterns with dots
        List<String> result2 = Tokenize.wordTokenize("19...", null, "newmm", false);
        assertEquals(Arrays.asList("19", "..."), result2);
        
        List<String> result3 = Tokenize.wordTokenize("19.", null, "newmm", false);
        assertEquals(Arrays.asList("19", "."), result3);
        
        List<String> result4 = Tokenize.wordTokenize("19.84", null, "newmm", false);
        assertEquals(Arrays.asList("19.84"), result4);
        
        // Test IP address
        List<String> result5 = Tokenize.wordTokenize("127.0.0.1", null, "newmm", false);
        assertEquals(Arrays.asList("127.0.0.1"), result5);
        
        // Test currency format
        List<String> result6 = Tokenize.wordTokenize("USD1,984.42", null, "newmm", false);
        assertEquals(Arrays.asList("USD", "1,984.42"), result6);
        
        // Test whitespace handling with keep_whitespace=true
        List<String> result7 = Tokenize.wordTokenize("สวัสดีครับ สบายดีไหมครับ", null, "newmm", true);
        assertEquals(Arrays.asList("สวัสดี", "ครับ", " ", "สบายดี", "ไหม", "ครับ"), result7);
        
        // Test specific Thai words
        List<String> result8 = Tokenize.wordTokenize("จุ๋มง่วงนอนยัง", null, "newmm", false);
        assertEquals(Arrays.asList("จุ๋ม", "ง่วงนอน", "ยัง"), result8);
        
        List<String> result9 = Tokenize.wordTokenize("จุ๋มง่วง", null, "newmm", false);
        assertEquals(Arrays.asList("จุ๋ม", "ง่วง"), result9);
        
        // Test multiple spaces with keep_whitespace=false
        List<String> result10 = Tokenize.wordTokenize("จุ๋ม   ง่วง", null, "newmm", false);
        assertEquals(Arrays.asList("จุ๋ม", "ง่วง"), result10);
        
        // Test that whitespace is not in output when keep_whitespace=false
        List<String> result11 = Tokenize.wordTokenize("จุ๋มง่วง", null, "newmm", false);
        assertFalse(result11.contains(" "));
        
        // Test parentheses
        List<String> result12 = Tokenize.wordTokenize("(คนไม่เอา)", null, "newmm", false);
        assertEquals(Arrays.asList("(", "คน", "ไม่", "เอา", ")"), result12);
        
        // Test slash
        List<String> result13 = Tokenize.wordTokenize("กม/ชม", null, "newmm", false);
        assertEquals(Arrays.asList("กม", "/", "ชม"), result13);
        
        // Test mixed Thai and parentheses
        List<String> result14 = Tokenize.wordTokenize("สีหน้า(รถ)", null, "newmm", false);
        assertEquals(Arrays.asList("สีหน้า", "(", "รถ", ")"), result14);
    }
}
