package kreslavskiy.wordlesolver;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WordleHandlerTest {
    @Test
    void validRequestReturnsJsonArray() {
        //given
        WordSolver solver = mock(WordSolver.class);
        when(solver.solve(org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of("crate", "grace"));
        WordleHandler handler = new WordleHandler(solver);
        APIGatewayV2HTTPEvent event = eventWithBody(
                "{\"guesses\":[{\"guess\":\"crane\",\"feedback\":\"bybgb\"}]}");

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(event, null);

        //then
        assertEquals(200, response.getStatusCode());
        assertEquals("[\"crate\",\"grace\"]", response.getBody());
    }

    @Test
    void uppercaseInputIsLowercasedBeforeSolving() {
        //given
        WordSolver solver = mock(WordSolver.class);
        when(solver.solve(org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of());
        WordleHandler handler = new WordleHandler(solver);
        APIGatewayV2HTTPEvent event = eventWithBody(
                "{\"guesses\":[{\"guess\":\"CRANE\",\"feedback\":\"BYBGB\"}]}");

        //when
        handler.handleRequest(event, null);

        //then
        verify(solver).solve(argThat(guesses -> guesses.size() == 1
                && guesses.get(0).getGuess().equals("crane")
                && guesses.get(0).getFeedback().equals("bybgb")));
    }

    @Test
    void missingBodyReturnsBadRequest() {
        //given
        WordleHandler handler = handlerForStatus();

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(eventWithBody(null), null);

        //then
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void invalidJsonReturnsBadRequest() {
        //given
        WordleHandler handler = handlerForStatus();

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(eventWithBody("{"), null);

        //then
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void missingGuessesReturnsBadRequest() {
        //given
        WordleHandler handler = handlerForStatus();

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(eventWithBody("{}"), null);

        //then
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void tooManyGuessesReturnsBadRequest() {
        //given
        String body = "{\"guesses\":[{}, {}, {}, {}, {}, {}, {}]}";
        WordleHandler handler = handlerForStatus();

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(eventWithBody(body), null);

        //then
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void invalidGuessReturnsBadRequest() {
        //given
        String body = "{\"guesses\":[{\"guess\":\"four\",\"feedback\":\"bbbbb\"}]}";
        WordleHandler handler = handlerForStatus();

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(eventWithBody(body), null);

        //then
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void invalidFeedbackReturnsBadRequest() {
        //given
        String body = "{\"guesses\":[{\"guess\":\"crane\",\"feedback\":\"xxxxx\"}]}";
        WordleHandler handler = handlerForStatus();

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(eventWithBody(body), null);

        //then
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void solverFailureReturnsStackTrace() {
        //given
        WordSolver solver = mock(WordSolver.class);
        when(solver.solve(org.mockito.ArgumentMatchers.anyList())).thenThrow(new IllegalStateException("failed"));
        WordleHandler handler = new WordleHandler(solver);
        APIGatewayV2HTTPEvent event = eventWithBody("{\"guesses\":[]}");

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(event, null);

        //then
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("java.lang.IllegalStateException: failed"));
    }

    private WordleHandler handlerForStatus() {
        return new WordleHandler(mock(WordSolver.class));
    }

    private APIGatewayV2HTTPEvent eventWithBody(String body) {
        return APIGatewayV2HTTPEvent.builder().withBody(body).build();
    }
}
