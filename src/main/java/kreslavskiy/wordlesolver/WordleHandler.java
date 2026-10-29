package kreslavskiy.wordlesolver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordleHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private static final String CONTENT_TYPE = "application/json";
    private final Gson gson;
    private final WordSolver wordSolver;

    public WordleHandler() {
        this(new WordSolver());
    }

    public WordleHandler(WordSolver wordSolver) {
        gson = new Gson();
        this.wordSolver = wordSolver;
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            WordleRequest request = parseRequest(event.getBody());
            List<GuessFeedback> guesses = normalizeAndValidate(request);
            return response(200, gson.toJson(wordSolver.solve(guesses)));
        } catch (InvalidRequestException | JsonSyntaxException exception) {
            return response(400, exception.getMessage());
        } catch (Exception exception) {
            return response(500, stackTrace(exception));
        }
    }

    private WordleRequest parseRequest(String body) {
        if (body == null || body.isBlank()) {
            throw new InvalidRequestException("Request body is missing");
        }

        try {
            WordleRequest request = gson.fromJson(body, WordleRequest.class);
            if (request == null || request.getGuesses() == null) {
                throw new InvalidRequestException("guesses is missing");
            }
            return request;
        } catch (JsonSyntaxException exception) {
            throw new InvalidRequestException("Request body is not valid JSON");
        }
    }

    private List<GuessFeedback> normalizeAndValidate(WordleRequest request) {
        List<GuessFeedback> guesses = request.getGuesses();
        if (guesses.size() > 6) {
            throw new InvalidRequestException("No more than 6 guesses are allowed");
        }

        for (GuessFeedback guessFeedback : guesses) {
            if (guessFeedback == null || guessFeedback.getGuess() == null
                    || guessFeedback.getFeedback() == null) {
                throw new InvalidRequestException("Guess and feedback are required");
            }

            String guess = guessFeedback.getGuess().toLowerCase(Locale.ROOT);
            String feedback = guessFeedback.getFeedback().toLowerCase(Locale.ROOT);
            if (!guess.matches("[a-z]{5}")) {
                throw new InvalidRequestException("Guess must be 5 letters a-z");
            }
            if (!feedback.matches("[byg]{5}")) {
                throw new InvalidRequestException("Feedback must be 5 characters from b, y, or g");
            }
            guessFeedback.setGuess(guess);
            guessFeedback.setFeedback(feedback);
        }
        return guesses;
    }

    private APIGatewayV2HTTPResponse response(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", CONTENT_TYPE);
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(body)
                .build();
    }

    private String stackTrace(Exception exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
