## OpenAI Function Call Example
From the project: https://github.com/HamaWhiteGG/langchain-java

Please refer to the [ChatFunctionTest](https://github.com/HamaWhiteGG/langchain-java/blob/dev/openai-client/src/test/java/com/hw/openai/function/ChatFunctionTest.java) for the complete test code.

## 1. Core main process
- Generates a ChatParameter instance that represents the given class.
- Invoke the OpenAI API and return the function name and parameters.
- Execute functions through the FunctionExecutor.

## 2. Step-by-step introduction
### 2.1 Declare function parameters and response
```java
public record Weather(
        @JsonProperty(required = true)
        @JsonPropertyDescription("The city and state, e.g. San Francisco, CA")
        String location,

        @JsonPropertyDescription("The temperature unit")
        WeatherUnit unit
) {}


public enum WeatherUnit {

    CELSIUS,
    FAHRENHEIT
}


@Data
@Builder
public class WeatherResponse {

    public String location;

    public WeatherUnit unit;

    public int temperature;

    public String description;
}


WeatherResponse getCurrentWeather(Weather weather) {
    // mock function
    return WeatherResponse.builder()
            .location(weather.location())
            .unit(weather.unit())
            .temperature(new Random().nextInt(50))
            .description("sunny")
            .build();
}
```


### 2.2 Generates a ChatParameter instance that represents the given class
Automatically convert `Weather.class` to `ChatParameter` using the `ChatParameterUtils.generate` method.
```java
ChatFunction.ChatParameter chatParameter = ChatParameterUtils.generate(Weather.class);
```
The output of converting `chatParameter` to JSON String is as follows:
```json
{
    "type" : "object",
    "properties" : {
        "location" : {
            "type" : "string",
            "description" : "The city and state, e.g. San Francisco, CA"
        },
        "unit" : {
            "type" : "string",
            "description" : "The temperature unit",
            "enum" : [
                "celsius",
                "fahrenheit"
            ]
        }
    },
    "required" : [
        "location"
    ]
}
```
### 2.3 Invoke the OpenAI API and return the function name and parameters.
```java
ChatFunction chatFunction = ChatFunction.builder()
        .name(functionName)
        .description("Get the current weather in a given location")
        .parameters(ChatParameterUtils.generate(Weather.class))
        .build();

Message message = Message.of("What is the weather like in Boston?");

ChatCompletion chatCompletion = ChatCompletion.builder()
        .model("gpt-4")
        .temperature(0)
        .messages(List.of(message))
        .tools(List.of(new Tool(chatFunction)))
        .toolChoice("auto")
        .build();

ChatCompletionResp response = client.createChatCompletion(chatCompletion);
ChatChoice chatChoice = response.getChoices().get(0);
```

The output of converting `chatChoice` to JSON String is as follows:
```json
{
    "index":0,
    "message":{
        "role":"assistant",
        "content":null,
        "tool_calls":[
            {
                "id":"call_aS4LspVVBkE8uIiBSLZWXe6O",
                "type":"function",
                "function":{
                    "name":"get_current_weather",
                    "arguments":"{\n  \"location\": \"Boston, MA\"\n}"
                }
            }
        ]
    },
    "finish_reason":"tool_calls"
}
```

### 2.4 Execute functions through the FunctionExecutor.
The code for `FunctionExecutor` is as follows, allowing the caller to avoid explicit type casting through generics, making the code more concise and elegant.
```java
public class FunctionExecutor {

    private static final Map<String, Function<?, ?>> FUNCTION_MAP = new HashMap<>(16);

    public static <T> void register(String functionName, Function<T, ?> function) {
        FUNCTION_MAP.put(functionName, function);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R execute(String functionName, Class<T> clazz, String arguments) throws ClassCastException {
        Function<T, R> function = (Function<T, R>) FUNCTION_MAP.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException("No function registered with name: " + functionName);
        }
        T input = convertFromJsonStr(arguments, clazz);
        return function.apply(input);
    }
}
```

The caller's code is as follows:
```java
Function function = chatChoice.getMessage().getToolCalls().get(0).getFunction();

// execute function
WeatherResponse weatherResponse = FunctionExecutor.execute(function.getName(), Weather.class, function.getArguments());
System.out.println(weatherResponse)
```

The final result is:
```shell
WeatherResponse(location=Boston, MA, unit=null, temperature=2, description=sunny)
```

