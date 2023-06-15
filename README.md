# 🦜️ LangChain Java

⚡ Building applications with LLMs through composability ⚡
 
## 1. What is this?
 
This is the Java language implementation of LangChain.

Large language models (LLMs) are emerging as a transformative technology, enabling developers to build applications that they previously could not. But using these LLMs in isolation is often not enough to create a truly powerful app - the real power comes when you can combine them with other sources of computation or knowledge.

This library is aimed at assisting in the development of those types of applications.

Looking for the Python version? Check out [LangChain](https://github.com/hwchase17/langchain).

## 2. Quickstart Guide
This tutorial gives you a quick walkthrough about building an end-to-end language model application with LangChain.

View the [Quickstart Guide](https://python.langchain.com/en/latest/getting_started/getting_started.html#) on the LangChain official website.

### 2.1 Maven Repository
Prerequisites for building:
* Java 17 or later
* Unix-like environment (we use Linux, Mac OS X)
* Maven (we recommend version 3.8.6 and require at least 3.5.4)

```xml
<dependency>
    <groupId>io.github.hamawhitegg</groupId>
    <artifactId>langchain-core</artifactId>
    <version>0.1.6</version>
</dependency>
```

### 2.2 Environment Setup
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
        .openaiApiKey("xxx")
        .openaiProxy("http://host:port")
        .build()
        .init();
```

---
> The following test code can be used to view the [QuickStart.java](langchain-core/src/test/java/com/hw/langchain/QuickStart.java)

### 2.3 LLMs: Get predictions from a language model
The most basic building block of LangChain is calling an LLM on some input. Let’s walk through a simple example of how to do this. For this purpose, let’s pretend we are building a service that generates a company name based on what the company makes.
```java
var llm = OpenAI.builder()
        .temperature(0.9f)
        .build()
        .init();

String text = "What would be a good company name for a company that makes colorful socks?";
System.out.println(llm.call(text));
```
```shell
Feetful of Fun
```

### 2.4 Prompt Templates: Manage prompts for LLMs
Calling an LLM is a great first step, but it’s just the beginning. Normally when you use an LLM in an application, you are not sending user input directly to the LLM. Instead, you are probably taking user input and constructing a prompt, and then sending that to the LLM.
```java
var prompt = new PromptTemplate(List.of("product"),
        "What is a good name for a company that makes {product}?");

System.out.println(prompt.format(Map.of("product", "colorful socks")));
```
```shell
What is a good name for a company that makes colorful socks?
```

### 2.5 Chains: Combine LLMs and prompts in multi-step workflows
Up until now, we’ve worked with the PromptTemplate and LLM primitives by themselves. But of course, a real application is not just one primitive, but rather a combination of them.

A chain in LangChain is made up of links, which can be either primitives like LLMs or other chains.

#### 2.5.1 LLM Chain
The most core type of chain is an LLMChain, which consists of a PromptTemplate and an LLM.
```java
var llm = OpenAI.builder()
        .temperature(0.9f)
        .build()
        .init();

var prompt = new PromptTemplate(List.of("product"),
        "What is a good name for a company that makes {product}?");

var chain = new LLMChain(llm, prompt);
System.out.println(chain.run("colorful socks"));
```
```shell
\n\nSocktastic!
```
#### 2.5.2 SQL Chain
This example demonstrates the use of the SQLDatabaseChain for answering questions over a database.
```java
var database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "xxx", "xxx");

var llm = OpenAI.builder()
        .temperature(0)
        .build()
        .init();

var chain = SQLDatabaseChain.fromLLM(llm, database);
System.out.println(chain.run("How many students are there?"));
```
```shell
There are 6 students.
```

### 2.6 Agents: Dynamically Call Chains Based on User Input
Agents no longer do: they use an LLM to determine which actions to take and in what order. An action can either be using a tool and observing its output, or returning to the user.

When used correctly agents can be extremely powerful. In this tutorial, we show you how to easily use agents through the simplest, highest level API.

Set the appropriate environment variables.
```shell
export SERPAPI_API_KEY=xxx
```

Now we can get started!
```java
var llm = OpenAI.builder()
        .temperature(0)
        .build()
        .init();

// load some tools to use.
var tools = loadTools(List.of("serpapi", "llm-math"), llm);

// initialize an agent with the tools, the language model, and the type of agent
var agent = initializeAgent(tools, llm, AgentType.ZERO_SHOT_REACT_DESCRIPTION);

// let's test it out!
String text =
        "What was the high temperature in SF yesterday in Fahrenheit? What is that number raised to the .023 power?";
System.out.println(agent.run(text));
```
```shell
I need to find the temperature first, then use the calculator to raise it to the .023 power.

Action: Search
Action Input: "High temperature in SF yesterday"
Observation: San Francisco Weather History for the Previous 24 Hours ; 60 °F · 60 °F · 61 °F ...

Thought: I now have the temperature, so I can use the calculator to raise it to the .023 power.
Action: Calculator
Action Input: 60^.023
Observation: Answer: 1.09874643447

Thought: I now know the final answer
Final Answer: 1.09874643447

1.09874643447
```

### 2.7 Memory: Add State to Chains and Agents
So far, all the chains and agents we’ve gone through have been stateless. 
But often, you may want a chain or agent to have some concept of "memory" so that it may remember information about 
its previous interactions. The clearest and simple example of this is when designing a chatBot - 
you want it to remember previous messages so it can use context from that to have a better conversation.

```java
var llm = OpenAI.builder()
        .temperature(0)
        .build()
        .init();

var conversation = new ConversationChain(llm);

var output = conversation.predict(Map.of("input", "Hi there!"));
System.out.println("Finished chain.\n'" + output + "'");

output = conversation.predict(Map.of("input", "I'm doing well! Just having a conversation with an AI."));
System.out.println("Finished chain.\n'" + output + "'");
```

```shell
The following is a friendly conversation between a human and an AI. The AI is talkative and provides lots of specific details from its context. If the AI does not know the answer to a question, it truthfully says it does not know.

Current conversation:

Human: Hi there!
AI:
Finished chain.
' Hi there! It's nice to meet you. How can I help you today?'

The following is a friendly conversation between a human and an AI. The AI is talkative and provides lots of specific details from its context. If the AI does not know the answer to a question, it truthfully says it does not know.

Current conversation:
Human: Hi there!
AI:  Hi there! It's nice to meet you. How can I help you today?
Human: I'm doing well! Just having a conversation with an AI.
AI:
Finished chain.
' That's great! It's always nice to have a conversation with someone new. What would you like to talk about?'
```
 
## 3. Run Test Cases from Source
```
git clone https://github.com/HamaWhiteGG/langchain-java.git
cd langchain-java

# export JAVA_HOME=JDK17_INSTALL_HOME && mvn clean test
mvn clean test
```

## 4. Apply Spotless
```
cd langchain-java

# export JAVA_HOME=JDK17_INSTALL_HOME && mvn spotless:apply
mvn spotless:apply
```

## 5. Support
Don’t hesitate to ask!

[Open an issue](https://github.com/HamaWhiteGG/langchain-java/issues) if you find a bug in Flink.

## 6. Fork and Contribute
This is an active open-source project. We are always open to people who want to use the system or contribute to it.

Contact me if you are looking for implementation tasks that fit your skills.


