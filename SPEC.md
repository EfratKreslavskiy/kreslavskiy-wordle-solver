# Wordle Solver: Design Spec

Java 21, Gradle, IntelliJ. Deployed as an AWS Lambda (Function URL, auth NONE). For now it is verified only with JUnit 5 and Mockito unit tests.

## Goal
Given a player's Wordle guesses and the colour feedback for each, return every word on the Wordle answer list that could still be the secret word.

## House rules
- No `static` variables except `static final` constants. Avoid `static` unless there is no good alternative; static methods only when clearly useful.
- No logic in `main`; all logic sits behind the Lambda handler. Leave any generated `Main` untouched.
- Short comments so a newcomer can follow. Do not edit or rename unrelated files or methods.
- Commit and push after every small change. No commit over 100 lines (the dictionary is exempt).
- Tests are JUnit 5 with `//given`, `//when`, `//then`; use Mockito `mock()` only where needed.

## API
Send a JSON body to the Function URL (any HTTP method is accepted for now). Letters may be any case; they are lowercased internally.

```json
{"guesses": [{"guess": "crane", "feedback": "bybgb"}, {"guess": "slate", "feedback": "bbygg"}]}
```

`feedback` is 5 characters: `g` green (right place), `y` yellow (in the word, wrong place), `b` grey (not in the word, or no more copies than the g/y ones in that guess).

Success is `200` with a plain JSON array of lowercase words in dictionary order, such as `["crate","grace"]`. No matches gives `200` and `[]`. Any 5-letter guess is accepted even if it is not in the dictionary; candidates come only from the dictionary.

| Situation | Response |
|---|---|
| Body missing or not valid JSON, or `guesses` missing | 400 with a plain-text reason |
| More than 6 guesses | 400 |
| Guess is not 5 letters a-z, or feedback is not 5 characters from b/y/g | 400 |
| Empty `guesses` list | 200 with the whole dictionary |
| Any other exception | 500 with the stack trace as the body |

## How a word is checked
For each dictionary word and each guess, work out the feedback Wordle would give if that word were the answer. Keep the word only if that equals the supplied feedback for every guess. This reproduces real duplicate-letter behaviour:
1. Mark greens (same letter, same position) and count the answer's unmatched letters.
2. Going left to right, each other guess letter is yellow if an unmatched copy remains (use it up), otherwise grey.

Examples: guess `crane` vs answer `crate` gives `gggbg`. Guess `speed` vs answer `abide` gives `bbyby` (the second `e` is grey because the answer has only one).

## Classes (package `kreslavskiy.wordlesolver`)
| Class | Role |
|---|---|
| `WordleHandler` | Lambda entry point (`RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>`). Parses with Gson, validates, lowercases, calls `WordSolver`, builds the response. No-arg constructor for AWS plus one taking a `WordSolver` for tests. |
| `WordleRequest`, `GuessFeedback` | Gson data classes for the body (`guesses`, `guess`, `feedback`). |
| `InvalidRequestException` | Thrown for bad input; the handler turns it into a 400. |
| `WordSolver` | `List<String> solve(List<GuessFeedback>)`: filters the dictionary using `GuessMatcher`. |
| `GuessMatcher` | `boolean matches(String candidate, String guess, String feedback)`: the algorithm above. |
| `WordList` | `List<String> getWords()`: the hardcoded dictionary. |

## Dictionary
- Official Wordle answer list only (~2,300 words), lowercase, hardcoded in `WordList` as instance data. A list this size fits in one class; the full ~13,000 guess list would hit the JVM's 64 KB per-method limit.
- Take the original list from a trusted copy (widely mirrored on GitHub). NYT has edited the live answers since, so it may differ slightly from today's game.

## Tests
- `GuessMatcherTest`: all green matches only that word; a grey letter rules out words containing it; yellow needs the letter elsewhere, not in that spot; the `speed`/`abide` case; wrong feedback returns false.
- `WordSolverTest` (mock `WordList`): no guesses returns all; one guess narrows; several guesses intersect; no match returns empty; order kept.
- `WordleHandlerTest` (mock `WordSolver`, event built with the library's builders): a valid request gives 200 and a JSON array; uppercase is lowercased before solving; every 400 row above; solver throwing gives 500 with a stack trace.
- `WordListTest`: every word matches `[a-z]{5}`; no duplicates.

## Commit plan (commit and push after each)
1. `SPEC.md`
2. `build.gradle`: Lambda core/events, Gson, shadow plugin (plus JUnit/Mockito if missing)
3. `GuessFeedback`, `WordleRequest`, `InvalidRequestException`
4. `GuessMatcher` + test (split if over 100 lines)
5. `WordList` with a few words + `WordListTest`
6. Full dictionary (exempt from the 100-line limit)
7. `WordSolver` + test
8. `WordleHandler` happy path + test
9. Handler 400 checks + tests
10. Handler 500 path + test

## Later: deploying
Dependencies: `aws-lambda-java-core:1.4.1`, `aws-lambda-java-events:3.16.1`, `gson:2.15.0`; plugin `com.gradleup.shadow` 8.3.11. Run `./gradlew shadowJar`, upload `build/libs/*-all.jar` to a Java 21 Lambda, set the handler to `kreslavskiy.wordlesolver.WordleHandler`, add a Function URL with auth NONE, then test with `curl -X POST -H "Content-Type: application/json" -d @request.json <url>`. JSON fields are camelCase, so Checkstyle needs no `CHECKSTYLE:OFF`.

## Defaults chosen where no preference was given
Missing `guesses` is a 400 but an empty list is fine; 6-guess maximum; plain-text 400 messages; response header `Content-Type: application/json`; the class breakdown above.
