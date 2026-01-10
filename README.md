# JThaiNLP
Thai Natural Language Processing library for Java

JThaiNLP is a Java library for Thai text processing with a PyThaiNLP-compatible API. It provides word tokenization using the newmm (New Maximum Matching) algorithm.

## Features

- **Word Tokenization**: Dictionary-based word segmentation using the newmm algorithm
- **Thai Character Cluster (TCC)**: Proper handling of Thai character boundaries
- **PyThaiNLP-compatible API**: Familiar interface for users of PyThaiNLP
- **Custom Dictionary Support**: Use your own word dictionary
- **Maven Support**: Easy integration into Java projects

## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.wannaphong</groupId>
    <artifactId>jthainlp</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or build from source:

```bash
git clone https://github.com/wannaphong/JThaiNLP.git
cd JThaiNLP
mvn clean install
```

## Usage

### Basic Tokenization

```java
import com.github.wannaphong.jthainlp.tokenize.Tokenize;
import java.util.List;

// Basic word tokenization
String text = "ไทยและภาษา";
List<String> tokens = Tokenize.wordTokenize(text);
System.out.println(tokens); // Output: [ไทย, และ, ภาษา]
```

### Custom Dictionary

```java
import com.github.wannaphong.jthainlp.tokenize.Tokenize;
import com.github.wannaphong.jthainlp.util.Trie;
import java.util.Arrays;
import java.util.List;

// Use custom dictionary
Trie customDict = new Trie(Arrays.asList("ไทย", "และ", "ภาษา", "ภาษาไทย"));
String text = "ไทยและภาษาไทย";
List<String> tokens = Tokenize.wordTokenize(text, customDict);
System.out.println(tokens); // Output: [ไทย, และ, ภาษาไทย]
```

### PyThaiNLP-Compatible API

```java
import com.github.wannaphong.jthainlp.tokenize.Tokenize;

// Similar to PyThaiNLP's word_tokenize function
List<String> tokens1 = Tokenize.wordTokenize(text);
List<String> tokens2 = Tokenize.wordTokenize(text, customDict);
List<String> tokens3 = Tokenize.wordTokenize(text, customDict, "newmm");
List<String> tokens4 = Tokenize.wordTokenize(text, customDict, "newmm", true);
```

### Running Examples

```bash
mvn compile exec:java -Dexec.mainClass="com.github.wannaphong.jthainlp.example.TokenizerExample"
```

## API Reference

### Tokenize.wordTokenize()

Main tokenization function with PyThaiNLP-compatible interface.

**Parameters:**
- `text` (String): Text to tokenize
- `customDict` (Trie, optional): Custom dictionary (null for default)
- `engine` (String, optional): Tokenization engine ("newmm" is default and currently only supported)
- `keepWhitespace` (boolean, optional): Whether to keep whitespace in output (default: true)

**Returns:**
- `List<String>`: List of word tokens

## Building and Testing

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package
```

## Implementation Details

### Newmm Algorithm

The newmm (New Maximum Matching) tokenizer implements:
- Dictionary-based maximal matching word segmentation
- Constrained by Thai Character Cluster (TCC) boundaries
- Graph-based approach with configurable cutoff to avoid exponential time
- Handling of non-Thai text (English, numbers, punctuation)

### Thai Character Cluster (TCC)

Thai text has special rules for where words can be broken. The TCC implementation respects Thai vowel and tone mark placement rules.

## License

Apache License 2.0 - See [LICENSE](LICENSE) file for details.

## References

- [PyThaiNLP](https://github.com/PyThaiNLP/pythainlp) - Thai NLP library for Python
- Original newmm implementation by Korakot Chaovavanich

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
