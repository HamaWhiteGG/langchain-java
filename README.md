# LangChain Java

 Building applications with LLMs through composability.
 
 ## What is this?
 
 This is the Java language implementation of LangChain.
 
 ## Examples
 Here's a simple example of using langchain-java to interact with OpenAI:
 
 ```java
@Test
void testOpenAiCall() {
    OpenAI openAI = OpenAI.builder()
            .proxy(ProxyUtils.http("127.0.0.1", 1087))
            .maxTokens(10)
            .build()
            .init();

    assertThat(openAI.call("Say foo:")).isEqualTo("\n\nFoo!");
}
 ```
 
## Run Test from Source
Prerequisites for building:
* Git
* Java 17
* Unix-like environment (we use Linux, Mac OS X)
* Maven (we recommend version 3.8.6 and require at least 3.5.4)

```
git clone https://github.com/HamaWhiteGG/langchain-java.git
cd langchain-java

# export JAVA_HOME=JDK17_INSTALL_DIR && mvn clean test
mvn clean test
```

## Support
Don’t hesitate to ask!
[Open an issue](https://github.com/HamaWhiteGG/langchain-java/issues) if you find a bug in Flink.


## Fork and Contribute
This is an active open-source project. We are always open to people who want to use the system or contribute to it.

Contact me if you are looking for implementation tasks that fit your skills.


