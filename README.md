# Wordle Solver

This project is a Java AWS Lambda wordle solver. Given one or more Wordle
guesses and their color feedback, it returns the words from its dictionary
that could still be the answer.

The deployed Lambda accepts a JSON request with this format:

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

## Test the deployed Lambda with curl

Create a file named `request.json` in the folder where you will run `curl`:

```json
{"guesses":[{"guess":"crane","feedback":"bybgb"}]}
```

Then run this command in **Command Prompt**:

```cmd
curl.exe -X POST ^
  -v ^
  -H "Content-Type: application/json" ^
  -d @request.json ^
  "https://kcu6xadvgzk3u7rfg456bp4qi40dcxqt.lambda-url.us-east-2.on.aws/"
```

The `^` characters continue the command onto the next line in Command Prompt.
The response will be a JSON array containing the possible answers.

You do not need to change into the project folder. Either run the command from
the folder containing `request.json`, or provide the file's full path:

```cmd
curl.exe -X POST ^
  -v ^
  -H "Content-Type: application/json" ^
  -d "@C:\path\to\request.json" ^
  "https://kcu6xadvgzk3u7rfg456bp4qi40dcxqt.lambda-url.us-east-2.on.aws/"
```

Opening the Lambda URL directly in a browser sends a GET request without a
JSON body, so the Lambda responds with `Request body is missing`. Use `curl`
or another API client to send the required POST body.