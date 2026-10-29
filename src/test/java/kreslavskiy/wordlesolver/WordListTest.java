package kreslavskiy.wordlesolver;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordListTest {
    private final WordList wordList = new WordList();

    @Test
    void everyWordIsFiveLowercaseLetters() {
        //given
        Set<String> words = new HashSet<>(wordList.getWords());

        //when
        boolean allValid = words.stream().allMatch(word -> word.matches("[a-z]{5}"));

        //then
        assertTrue(allValid);
    }

    @Test
    void wordsHaveNoDuplicates() {
        //given
        int wordCount = wordList.getWords().size();

        //when
        int uniqueWordCount = new HashSet<>(wordList.getWords()).size();

        //then
        assertEquals(wordCount, uniqueWordCount);
    }
}
