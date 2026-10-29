package kreslavskiy.wordlesolver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuessMatcherTest {
    private final GuessMatcher matcher = new GuessMatcher();

    @Test
    void matchesAllGreenLetters() {
        //given
        String candidate = "crate";

        //when
        boolean matches = matcher.matches(candidate, "crate", "ggggg");

        //then
        assertTrue(matches);
    }

    @Test
    void greyLetterRulesOutCandidateLetter() {
        //given
        String candidate = "crate";

        //when
        boolean matches = matcher.matches(candidate, "crane", "gggbb");

        //then
        assertFalse(matches);
    }

    @Test
    void yellowLetterMustBeElsewhere() {
        //given
        String candidate = "crate";

        //when
        boolean matches = matcher.matches(candidate, "tarot", "yyybb");

        //then
        assertTrue(matches);
    }

    @Test
    void handlesDuplicateLetters() {
        //given
        String candidate = "abide";

        //when
        boolean matches = matcher.matches(candidate, "speed", "bbyby");

        //then
        assertTrue(matches);
    }

    @Test
    void rejectsWrongFeedback() {
        //given
        String candidate = "crate";

        //when
        boolean matches = matcher.matches(candidate, "crane", "gggbb");

        //then
        assertFalse(matches);
    }
}
