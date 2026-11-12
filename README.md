# Wordle Solver

This project is a Java AWS Lambda Wordle solver. Given one or more Wordle
guesses and their color feedback, it returns the words from its dictionary
that could still be the answer.

## Request format

The Lambda accepts a JSON request with this format:

```json
{
  "guesses": [
    {
      "guess": "crane",
      "feedback": "bybgb"
    }
  ]
}
```

Feedback characters use:

- `g` for green: the letter is correct and in the correct position
- `y` for yellow: the letter is in the answer but in a different position
- `b` for black/gray: the letter is not available in the answer

## Current AWS Lambda deployment

The current public Function URL is configured with no authentication:

<https://4l3s2jlxd7y6f2t65uenwkfpki0nllcz.lambda-url.us-east-2.on.aws/>

| Setting | Value |
| --- | --- |
| Region | `us-east-2` (US East, Ohio) |
| Function name | `wordleSolver` |
| Runtime | Java 25 |
| Architecture | `x86_64` |
| Handler | `kreslavskiy.wordlesolver.WordleHandler::handleRequest` |
| Function URL authentication | `NONE` (public) |
| Invoke mode | `BUFFERED` |
| Deployment artifact | `build/libs/kreslavskiy-wordle-solver-1.0-SNAPSHOT-all.jar` |

## Test the deployed Lambda

Create or use `request.json` in the folder where you run the command:

```json
{
  "guesses": [
    {
      "guess": "crane",
      "feedback": "bybgb"
    }
  ]
}
```

In **Command Prompt**, run:

```cmd
set "FUNCTION_URL=https://4l3s2jlxd7y6f2t65uenwkfpki0nllcz.lambda-url.us-east-2.on.aws/"
curl.exe -X POST ^
  -H "Content-Type: application/json" ^
  -d @request.json ^
  "%FUNCTION_URL%"
```

The response is a JSON array containing the possible answers. Opening the
Function URL directly in a browser sends a GET request without a JSON body,
so the Lambda responds with `Request body is missing`.

Because the Function URL is public, do not include credentials, tokens,
personal information, or other sensitive data in requests or logs. Use only
harmless test payloads.
