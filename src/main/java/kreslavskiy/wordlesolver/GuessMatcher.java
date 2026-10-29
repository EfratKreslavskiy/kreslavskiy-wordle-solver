package kreslavskiy.wordlesolver;

public class GuessMatcher {
    public boolean matches(String candidate, String guess, String feedback) {
        char[] result = new char[guess.length()];
        int[] unmatchedLetters = new int[26];

        for (int position = 0; position < guess.length(); position++) {
            if (guess.charAt(position) == candidate.charAt(position)) {
                result[position] = 'g';
            } else {
                unmatchedLetters[candidate.charAt(position) - 'a']++;
            }
        }

        for (int position = 0; position < guess.length(); position++) {
            if (result[position] == 'g') {
                continue;
            }

            int letterIndex = guess.charAt(position) - 'a';
            if (unmatchedLetters[letterIndex] > 0) {
                result[position] = 'y';
                unmatchedLetters[letterIndex]--;
            } else {
                result[position] = 'b';
            }
        }

        return new String(result).equals(feedback);
    }
}
