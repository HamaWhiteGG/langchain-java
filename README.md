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

### 2.1 Installation 
Prerequisites for building:
* Git
* Java 17 or later
* Unix-like environment (we use Linux, Mac OS X)
* Maven (we recommend version 3.8.6 and require at least 3.5.4)

```
git clone https://github.com/HamaWhiteGG/langchain-java.git
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
OpenAI llm = OpenAI.builder()
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
OpenAI llm = OpenAI.builder()
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
PromptTemplate prompt = new PromptTemplate(List.of("product"),
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
OpenAI llm = OpenAI.builder()
        .temperature(0.9f)
        .build()
        .init();

PromptTemplate prompt = new PromptTemplate(List.of("product"),
        "What is a good name for a company that makes {product}?");

Chain chain = new LLMChain(llm, prompt);
System.out.println(chain.run("colorful socks"));
```
```shell
\n\nSocktastic!
```
#### 2.5.2 SQL Chain
This example demonstrates the use of the SQLDatabaseChain for answering questions over a database.
```java
SQLDatabase database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "xxx", "xxx");

BaseLanguageModel llm = OpenAI.builder()
        .temperature(0)
        .build()
        .init();

Chain chain = SQLDatabaseChain.fromLLM(llm, database);
System.out.println(chain.run("How many students are there?"));
```
```shell
There are 6 students.
```
 
## 3. Run Test Cases from Source
```
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


