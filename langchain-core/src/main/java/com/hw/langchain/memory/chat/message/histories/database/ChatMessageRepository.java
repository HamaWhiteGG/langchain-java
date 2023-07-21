package com.hw.langchain.memory.chat.message.histories.database;

import com.hw.langchain.schema.BaseMessage;
import java.util.List;

/**
 * interface for database supported chat message repository;
 * @author zhangxiaojia002
 * @date 2023/7/20 9:50 下午
 **/
public interface ChatMessageRepository {

	/**
	 * load all history chat message of given sessionId
	 * @param sessionId
	 * @return
	 */
	List<BaseMessage> loadMessage(String sessionId);

	void saveMessage(String sessionId, BaseMessage baseMessage);

	void clearSessionChatMessage(String sessionId);
}
