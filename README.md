# 🦜️ LangChain.Java

Java version of LangChain, while empowering LLM for Big Data.

> If you are interested, you can add me on WeChat: HamaWhite, or send email to [me](mailto:baisongxx@gmail.com).

## 1. What is this?

This is the Java language implementation of LangChain.

Large language models (LLMs) are emerging as a transformative technology, enabling developers to build applications that they previously could not. But using these LLMs in isolation is often not enough to create a truly powerful app - the real power comes when you can combine them with other sources of computation or knowledge.

This library is aimed at assisting in the development of those types of applications. 


The following example can view in the [langchain-example](langchain-examples/src/main/java/com/hw/langchain/examples)

## 2. User cases
- [SQL Chains](langchain-examples/src/main/java/com/hw/langchain/examples/chains/SqlChainExample.java)
- [API Chains](langchain-examples/src/main/java/com/hw/langchain/examples/chains/ApiChainExample.java)
- [QA-Milvus-Text](langchain-examples/src/main/java/com/hw/langchain/examples/chains/MilvusExample.java)
- [QA-Pinecone-Text](langchain-examples/src/main/java/com/hw/langchain/examples/chains/RetrievalQaExample.java)
- [QA-Pinecone-Markdown](langchain-examples/src/main/java/com/hw/langchain/examples/chains/RetrievalMarkdownExample.java)
- [Summarization](langchain-examples/src/main/java/com/hw/langchain/examples/chains/SummarizationExample.java)
- [Agent with Google Search](langchain-examples/src/main/java/com/hw/langchain/examples/agents/LlmAgentExample.java)
- [Spark SQL AI](langchain-bigdata/langchain-spark/src/test/java/com/hw/langchain/agents/toolkits/spark/sql/toolkit/SparkSqlToolkitTest.java)
- [Flink SQL AI](langchain-bigdata/langchain-flink/src/test/java/com/hw/langchain/agents/toolkits/flink/sql/toolkit/FlinkSqlToolkitTest.java)

## 3. Integrations

### 3.1 LLMs
- [OpenAI](langchain-examples/src/main/java/com/hw/langchain/examples/llms/OpenAIExample.java), (support [stream](langchain-examples/src/main/java/com/hw/langchain/examples/llms/StreamOpenAIExample.java))
- [Azure OpenAI](openai-client/src/test/java/com/hw/openai/AzureOpenAiClientTest.java)
- [ChatGLM2](langchain-examples/src/main/java/com/hw/langchain/examples/llms/ChatGLMExample.java)
- [Ollama](langchain-examples/src/main/java/com/hw/langchain/examples/llms/OllamaExample.java)

### 3.2 Vector stores
- [Pinecone](langchain-examples/src/main/java/com/hw/langchain/examples/vectorstores/PineconeExample.java)
- [Milvus](langchain-examples/src/main/java/com/hw/langchain/examples/chains/MilvusExample.java)

## 4. Quickstart Guide
This tutorial gives you a quick walkthrough about building an end-to-end language model application with LangChain.

The API documentation is available at the following link:   
[https://hamawhitegg.github.io/langchain-java](https://hamawhitegg.github.io/langchain-java)

### 4.1 Maven Repository
Prerequisites for building:
* Java 17 or later
* Unix-like environment (we use Linux, Mac OS X)
* Maven (we recommend version 3.8.6 and require at least 3.5.4)

 [![Maven Central](https://img.shields.io/maven-central/v/io.github.hamawhitegg/langchain-core)](https://maven-badges.herokuapp.com/maven-central/io.github.hamawhitegg/langchain-core)
```xml
<dependency>
    <groupId>io.github.hamawhitegg</groupId>
    <artifactId>langchain-core</artifactId>
    <version>0.2.0</version>
</dependency>
```

### 4.2 Environment Setup
Using LangChain will usually require integrations with one or more model providers, data stores, apis, etc. 
For this example, we will be using OpenAI’s APIs.

We will then need to set the environment variable.
```shell
export OPENAI_API_KEY=xxx

# If a proxy is needed, set the OPENAI_PROXY environment variable.
export OPENAI_PROXY=http://host:port
```

If you want to set the API key and proxy dynamically, you can use the openaiApiKey and openaiProxy parameter when initiating OpenAI class.
```java
var llm = OpenAI.builder()
        .openaiOrganization("xxx")
        .openaiApiKey("xxx")
        .openaiProxy("http://host:port")
        .requestTimeout(16)
        .build()
        .init();
```

### 4.3 LLMs
Get predictions from a language model. The basic building block of LangChain is the LLM, which takes in text and generates more text.

```java
var llm = OpenAI.builder()
        .temperature(0.9f)
        .build()
        .init();

var result = llm.predict("What would be a good company name for a company that makes colorful socks?");
print(result);
```
And now we can pass in text and get predictions!
```shell
Feetful of Fun
```
### 4.4 Chat models

Chat models are a variation on language models. While chat models use language models under the hood, the interface they expose is a bit different: rather than expose a "text in, text out" API, they expose an interface where "chat messages" are the inputs and outputs.


```java
var chat = ChatOpenAI.builder()
        .temperature(0)
        .build()
        .init();

var result = chat.predictMessages(List.of(new HumanMessage("Translate this sentence from English to French. I love programming.")));
println(result);
```

```shell
AIMessage{content='J'adore la programmation.', additionalKwargs={}}
```

It is useful to understand how chat models are different from a normal LLM, but it can often be handy to just be able to treat them the same. LangChain makes that easy by also exposing an interface through which you can interact with a chat model as you would a normal LLM. You can access this through the `predict` interface.

```java
var output = chat.predict("Translate this sentence from English to French. I love programming.");
println(output);
```
```shell
J'adore la programmation.
```

### 4.5 Prompt Templates
Most LLM applications do not pass user input directly into an LLM. Usually they will add the user input to a larger piece of text, called a prompt template, that provides additional context on the specific task at hand.

In the previous example, the text we passed to the model contained instructions to generate a company name. For our application, it'd be great if the user only had to provide the description of a company/product, without having to worry about giving the model instructions.

#### 4.5.1 LLMs

With PromptTemplates this is easy! In this case our template would be very simple:

```java
var prompt = PromptTemplate.fromTemplate("What is a good name for a company that makes {product}?");
var output = prompt.format(Map.of("product", "colorful socks"));
println(output)
```
```shell
What is a good name for a company that makes colorful socks?
```

#### 4.5.2 Chat models

Similar to LLMs, you can make use of templating by using a `MessagePromptTemplate`. You can build a `ChatPromptTemplate` from one or more `MessagePromptTemplate`s. You can use `ChatPromptTemplate`'s `formatMessages` method to generate the formatted messages.

```java
var template = "You are a helpful assistant that translates {input_language} to {output_language}.";
var systemMessagePrompt = SystemMessagePromptTemplate.fromTemplate(template);

var humanTemplate = "{text}";
var humanMessagePrompt = HumanMessagePromptTemplate.fromTemplate(humanTemplate);

var chatPrompt = ChatPromptTemplate.fromMessages(List.of(systemMessagePrompt, humanMessagePrompt));
var output = chatPrompt.formatMessages(Map.of("input_language", "English", "output_language", "French",
                        "text", "I love programming."));
println(output);
```

```shell
[
    SystemMessage{content='You are a helpful assistant that translates English to French.', additionalKwargs={}},         
    HumanMessage{content='I love programming.', additionalKwargs={}}
]
```

### 4.6 Chains

Now that we've got a model and a prompt template, we'll want to combine the two. Chains give us a way to link (or chain) together multiple primitives, like models, prompts, and other chains.

#### 4.6.1 LLMs
The simplest and most common type of chain is an LLMChain, which passes an input first to a PromptTemplate and then to an LLM. We can construct an LLM chain from our existing model and prompt template.
```java
var chain = new LLMChain(llm, prompt);
var result = chain.run("colorful socks");
println(result);
```
```shell
Feetful of Fun
```
#### 4.6.2 Chat models
The `LLMChain` can be used with chat models as well:
```java
var chain = new LLMChain(chat, chatPrompt);
var result = chain.run(Map.of("input_language", "English", "output_language", "French", "text", "I love programming."));
println(result);
```
```shell
J'adore la programmation.
```

### 4.7 Agents
Our first chain ran a pre-determined sequence of steps. To handle complex workflows, we need to be able to dynamically choose actions based on inputs.

Agents do just this: they use a language model to determine which actions to take and in what order. Agents are given access to tools, and they repeatedly choose a tool, run the tool, and observe the output until they come up with a final answer.

Set the appropriate environment variables.
```shell
export SERPAPI_API_KEY=xxx
```

#### 4.7.1 LLMs

```java
//  The language model we're going to use to control the agent.
var llm = OpenAI.builder().temperature(0).build().init();

// The tools we'll give the Agent access to. Note that the 'llm-math' tool uses an LLM, so we need to pass that in.
var tools = loadTools(List.of("serpapi", "llm-math"), llm);

//  Finally, let's initialize an agent with the tools, the language model, and the type of agent we want to use.
var agent = initializeAgent(tools, llm, AgentType.ZERO_SHOT_REACT_DESCRIPTION);

// Let's test it out!
agent.run("What was the high temperature in SF yesterday in Fahrenheit? What is that number raised to the .023 power?");
```
```shell
Thought: I need to find the temperature first, then use the calculator to raise it to the .023 power.
Action: Search
Action Input: "High temperature in SF yesterday"
Observation: High: 69.8ºf @12:30 PM Low: 55.94ºf @3:56 AM Approx. Precipitation / Rain Total: in. 1hr.

Thought: I now need to use the calculator to raise 69.8 to the .023 power
Action: Calculator
Action Input: 69.8^.023
Observation:  Answer: 1.10257635505

Thought: I now know the final answer
Final Answer: 1.10257635505
```

#### 4.7.2 Chat models

Agents can also be used with chat models, you can initialize one using `AgentType.CHAT_ZERO_SHOT_REACT_DESCRIPTION` as the agent type.

```java
//  The language model we're going to use to control the agent.
var chat = ChatOpenAI.builder().temperature(0).build().init();

// The tools we'll give the Agent access to. Note that the 'llm-math' tool uses an LLM, so we need to pass that in.
var llm = OpenAI.builder().temperature(0).build().init();
var tools = loadTools(List.of("serpapi", "llm-math"), llm);

//  Finally, let's initialize an agent with the tools, the language model, and the type of agent we want to use.
var agent = initializeAgent(tools, chat, AgentType.CHAT_ZERO_SHOT_REACT_DESCRIPTION);

// Now let's test it out!
agent.run("Who is Olivia Wilde's boyfriend? What is his current age raised to the 0.23 power?");
```

### 4.8 Memory

The chains and agents we've looked at so far have been stateless, but for many applications it's necessary to reference past interactions. This is clearly the case with a chatbot for example, where you want it to understand new messages in the context of past messages.

The Memory module gives you a way to maintain application state. The base Memory interface is simple: it lets you update state given the latest run inputs and outputs and it lets you modify (or contextualize) the next input using the stored state.

There are a number of built-in memory systems. The simplest of these is a buffer memory which just prepends the last few inputs/outputs to the current input - we will use this in the example below.

#### 4.8.1 LLMs

```java
var llm = OpenAI.builder().temperature(0).build().init();
var conversation = new ConversationChain(llm);

var output = conversation.run("Hi there!");
println(output);
```
here's what's going on under the hood

```shell
The following is a friendly conversation between a human and an AI. The AI is talkative and provides lots of specific details from its context. If the AI does not know the answer to a question, it truthfully says it does not know.

Current conversation:

Human: Hi there!
AI:
 Hi there! It's nice to meet you. How can I help you today?
```

Now if we run the chain again

```java
output = conversation.run("I'm doing well! Just having a conversation with an AI.");
println(output);
```

we'll see that the full prompt that's passed to the model contains the input and output of our first interaction, along with our latest input

```shell
The following is a friendly conversation between a human and an AI. The AI is talkative and provides lots of specific details from its context. If the AI does not know the answer to a question, it truthfully says it does not know.

Current conversation:
Human: Hi there!
AI:  Hi there! It's nice to meet you. How can I help you today?
Human: I'm doing well! Just having a conversation with an AI.
AI:
 That's great! It's always nice to have a conversation with someone new. What would you like to talk about?
```

#### 4.8.2 Chat models

You can use Memory with chains and agents initialized with chat models. The main difference between this and Memory for LLMs is that rather than trying to condense all previous messages into a string, we can keep them as their own unique memory object.

```java
var prompt = ChatPromptTemplate.fromMessages(List.of(
        SystemMessagePromptTemplate.fromTemplate(
                "The following is a friendly conversation between a human and an AI. The AI is talkative and " +
                        "provides lots of specific details from its context. If the AI does not know the " +
                        "answer to a question, it truthfully says it does not know."),
        new MessagesPlaceholder("history"),
        HumanMessagePromptTemplate.fromTemplate("{input}")));

var chat = ChatOpenAI.builder().temperature(0).build().init();
var memory = new ConversationBufferMemory(true);
var conversation = new ConversationChain(chat, prompt, memory);

var output = conversation.predict(Map.of("input", "Hi there!"));
println(output);
```

```shell
Hello! How can I assist you today?
```

```java
output = conversation.predict(Map.of("input", "I'm doing well! Just having a conversation with an AI."));
println(output);
```

```shell
That's great to hear! I'm here to chat and answer any questions you may have. What's on your mind?
```

```java
output = conversation.predict(Map.of("input", "Tell me about yourself."));
println(output);
```

```shell
Certainly! I am an AI language model developed by OpenAI called GPT-3. I have been trained on a vast amount of text data from the internet, which allows me to generate human-like responses to a wide range of queries and engage in conversations. My purpose is to assist and provide information to the best of my abilities. Is there anything specific you would like to know about me?
```

## 5. Run Test Cases from Source

```shell
git clone https://github.com/HamaWhiteGG/langchain-java.git
cd langchain-java

# export JAVA_HOME=JDK17_INSTALL_HOME && mvn clean test
mvn clean test
```

This project uses Spotless to format the code. If you make any modifications, please remember to format the code using the following command.

```shell
# export JAVA_HOME=JDK17_INSTALL_HOME && mvn spotless:apply
mvn spotless:apply
```

## 6. i18n for SQLDatabaseChain

If you want to choose other language instead english, just set environment variable on your host. If you not set, then **en-US** will be default
```shell
export USE_LANGUAGE=pt_BR
```

#### 6.1 Available Languages
| Language           | Value |
|--------------------|-------|
| English(default)   | en_US |
| Portuguese(Brazil) | pt_BR |

## 7. Support
Don’t hesitate to ask!

[Open an issue](https://github.com/HamaWhiteGG/langchain-java/issues) if you find a bug in langchain-java.

## 8. Fork and Contribute
This is an active open-source project. We are always open to people who want to use the system or contribute to it. Please note that pull requests should be merged into the **dev** branch.

Contact [me](mailto:baisongxx@gmail.com) if you are looking for implementation tasks that fit your skills.

