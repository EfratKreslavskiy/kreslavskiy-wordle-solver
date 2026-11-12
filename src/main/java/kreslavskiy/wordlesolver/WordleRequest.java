package kreslavskiy.wordlesolver;

import java.util.List;

public class WordleRequest {
    private List<GuessFeedback> guesses;

    public WordleRequest() {
    }

    public WordleRequest(List<GuessFeedback> guesses) {
        this.guesses = guesses;
    }

    public List<GuessFeedback> getGuesses() {
        return guesses;
    }

    public void setGuesses(List<GuessFeedback> guesses) {
        this.guesses = guesses;
    }
}
