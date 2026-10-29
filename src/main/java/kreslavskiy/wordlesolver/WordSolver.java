package kreslavskiy.wordlesolver;

import java.util.ArrayList;
import java.util.List;

public class WordSolver {
    private final WordList wordList;
    private final GuessMatcher guessMatcher;

    public WordSolver() {
        this(new WordList());
    }

    public WordSolver(WordList wordList) {
        this.wordList = wordList;
        guessMatcher = new GuessMatcher();
    }

    public List<String> solve(List<GuessFeedback> guesses) {
        List<String> matches = new ArrayList<>();
        for (String candidate : wordList.getWords()) {
            if (matchesEveryGuess(candidate, guesses)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    private boolean matchesEveryGuess(String candidate, List<GuessFeedback> guesses) {
        for (GuessFeedback guess : guesses) {
            if (!guessMatcher.matches(candidate, guess.getGuess(), guess.getFeedback())) {
                return false;
            }
        }
        return true;
    }
}
