package kreslavskiy.wordlesolver;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordleHandlerTest {
    @Test
    void evaluatesRequestAgainstTheRealDictionary() {
        //given
        WordleHandler handler = new WordleHandler();
        APIGatewayV2HTTPEvent event = eventWithBody(
                "{\"guesses\":[{\"guess\":\"CRATE\",\"feedback\":\"GGGGG\"}]}");

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(event, null);

        //then
        assertEquals(200, response.getStatusCode());
        assertEquals("[\"crate\"]", response.getBody());
    }

    @Test
    void evaluatesMixedFeedbackAgainstTheRealDictionary() {
        //given
        WordleHandler handler = new WordleHandler();
        APIGatewayV2HTTPEvent event = eventWithBody(
                "{\"guesses\":[{\"guess\":\"CRATE\",\"feedback\":\"BYYYY\"}]}");

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(event, null);

        //then
        assertEquals(200, response.getStatusCode());
        assertEquals("[\"after\",\"alert\",\"alter\",\"avert\",\"eater\",\"extra\",\"hater\","
                + "\"later\",\"taker\",\"tamer\",\"taper\",\"terra\",\"water\"]", response.getBody());
    }

    @Test
    void emptyRequestReturnsTheWholeRealDictionary() {
        //given
        WordleHandler handler = new WordleHandler();
        APIGatewayV2HTTPEvent event = eventWithBody("{\"guesses\":[]}");

        //when
        APIGatewayV2HTTPResponse response = handler.handleRequest(event, null);

        //then
        assertEquals(200, response.getStatusCode());
        assertEquals(2315, response.getBody().split(",").length);
    }

    private APIGatewayV2HTTPEvent eventWithBody(String body) {
        return APIGatewayV2HTTPEvent.builder().withBody(body).build();
    }
}
