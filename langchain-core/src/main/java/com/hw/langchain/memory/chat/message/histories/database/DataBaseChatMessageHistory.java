package com.hw.langchain.memory.chat.message.histories.database;

import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.BaseMessage;
import java.util.List;

/**
 * database based chat message history;
 * @author zhangxiaojia002
 * @date 2023/7/20 9:53 下午
 **/
public class DataBaseChatMessageHistory extends BaseChatMessageHistory {
	private final String sessionId;
	private final ChatMessageRepository chatMessageRepository;

	public DataBaseChatMessageHistory(String sessionId, ChatMessageRepository chatMessageRepository) {
		this.sessionId = sessionId;
		this.chatMessageRepository = chatMessageRepository;
	}

	@Override
	public void addMessage(BaseMessage message) {
		chatMessageRepository.saveMessage(sessionId, message);
	}

	@Override
	public void clear() {
		chatMessageRepository.clearSessionChatMessage(sessionId);
	}

	@Override
	public List<BaseMessage> getMessages() {
		return chatMessageRepository.loadMessage(sessionId);
	}
}
