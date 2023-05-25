package com.hw.langchain.llms.openai;

/**
 * Wrapper around OpenAI large language models.
 * <p>
 * To use, you should have the environment variable "OPENAI_API_KEY" set with your API key.
 * <p>
 * Any parameters that are valid to be passed to the openai.create call can be passed
 * in, even if not explicitly saved on this class.
 * <p>
 * Example:
 * <p>
 *      OpenAI openai = new OpenAI("text-davinci-003");
 *
 * @author: HamaWhite
 */
public class OpenAI extends BaseOpenAI {
}
