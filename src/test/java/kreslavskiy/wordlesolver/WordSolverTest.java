package kreslavskiy.wordlesolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WordSolverTest {
    @Test
    void noGuessesReturnsAllWordsInOrder() {
        //given
        WordList wordList = mock(WordList.class);
        List<String> words = List.of("crate", "grace", "slate");
        when(wordList.getWords()).thenReturn(words);
        WordSolver solver = new WordSolver(wordList);

        //when
        List<String> matches = solver.solve(List.of());

        //then
        assertEquals(words, matches);
    }

    @Test
    void oneGuessNarrowsDictionary() {
        //given
        WordList wordList = mock(WordList.class);
        when(wordList.getWords()).thenReturn(List.of("crate", "grace", "slate"));
        WordSolver solver = new WordSolver(wordList);
        List<GuessFeedback> guesses = List.of(new GuessFeedback("crate", "ggggg"));

        //when
        List<String> matches = solver.solve(guesses);

        //then
        assertEquals(List.of("crate"), matches);
    }

    @Test
    void severalGuessesAreIntersected() {
        //given
        WordList wordList = mock(WordList.class);
        when(wordList.getWords()).thenReturn(List.of("crate", "grace", "slate"));
        WordSolver solver = new WordSolver(wordList);
        List<GuessFeedback> guesses = List.of(
                new GuessFeedback("crate", "ggggg"),
                new GuessFeedback("slate", "bbggg")
        );

        //when
        List<String> matches = solver.solve(guesses);

        //then
        assertEquals(List.of("crate"), matches);
    }

    @Test
    void noMatchReturnsEmptyList() {
        //given
        WordList wordList = mock(WordList.class);
        when(wordList.getWords()).thenReturn(List.of("crate", "grace"));
        WordSolver solver = new WordSolver(wordList);
        List<GuessFeedback> guesses = List.of(new GuessFeedback("crate", "bbbbb"));

        //when
        List<String> matches = solver.solve(guesses);

        //then
        assertEquals(List.of(), matches);
    }
}
