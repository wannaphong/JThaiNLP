package com.github.wannaphong.jthainlp.example;

import com.github.wannaphong.jthainlp.tokenize.Tokenize;
import com.github.wannaphong.jthainlp.util.Trie;

import java.util.Arrays;
import java.util.List;

/**
 * Example usage of JThaiNLP tokenizer.
 */
public class TokenizerExample {
    
    public static void main(String[] args) {
        System.out.println("=== JThaiNLP Tokenizer Example ===\n");
        
        // Example 1: Basic tokenization
        System.out.println("Example 1: Basic tokenization");
        String text1 = "ไทยและภาษา";
        List<String> tokens1 = Tokenize.wordTokenize(text1);
        System.out.println("Text: " + text1);
        System.out.println("Tokens: " + tokens1);
        System.out.println();
        
        // Example 2: Tokenization with custom dictionary
        System.out.println("Example 2: Custom dictionary");
        String text2 = "ไทยและภาษาไทย";
        Trie customDict = new Trie(Arrays.asList("ไทย", "และ", "ภาษา", "ภาษาไทย"));
        List<String> tokens2 = Tokenize.wordTokenize(text2, customDict);
        System.out.println("Text: " + text2);
        System.out.println("Tokens: " + tokens2);
        System.out.println();
        
        // Example 3: Mixed Thai and English
        System.out.println("Example 3: Mixed Thai and English");
        String text3 = "ไทย NLP library";
        List<String> tokens3 = Tokenize.wordTokenize(text3);
        System.out.println("Text: " + text3);
        System.out.println("Tokens: " + tokens3);
        System.out.println();
        
        // Example 4: Keep vs remove whitespace
        System.out.println("Example 4: Keep vs remove whitespace");
        String text4 = "ไทย และ ภาษา";
        List<String> tokensWithSpace = Tokenize.wordTokenize(text4, null, "newmm", true);
        List<String> tokensNoSpace = Tokenize.wordTokenize(text4, null, "newmm", false);
        System.out.println("Text: " + text4);
        System.out.println("Tokens (with whitespace): " + tokensWithSpace);
        System.out.println("Tokens (no whitespace): " + tokensNoSpace);
        System.out.println();
        
        // Example 5: Longer text
        System.out.println("Example 5: Longer text");
        String text5 = "ประเทศไทยมีภาษาไทยเป็นภาษาราชการ";
        List<String> tokens5 = Tokenize.wordTokenize(text5);
        System.out.println("Text: " + text5);
        System.out.println("Tokens: " + tokens5);
    }
}
