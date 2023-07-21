package com.hw.langchain.memory.chat.message.histories.database.redis;

import com.hw.langchain.memory.chat.message.histories.database.DataBaseChatMessageHistory;
import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.BaseMessage;
import java.util.List;
import org.redisson.api.RedissonClient;

/**
 * a simple wrapper for DataBaseChatMessageHistory with redisChatMessageRepository;
 *
 * @author zhangxiaojia002
 * @date 2023/7/21 10:49 上午
 **/
public class RedisChatMessageHistory extends BaseChatMessageHistory {

	private DataBaseChatMessageHistory dataBaseChatMessageHistory;

	public RedisChatMessageHistory(String sessionId, RedissonClient redissonClient, int ttl) {
		RedisChatMessageRepository redisChatMessageRepository = new RedisChatMessageRepository(redissonClient, ttl);
		dataBaseChatMessageHistory = new DataBaseChatMessageHistory(sessionId, redisChatMessageRepository);
	}

	public RedisChatMessageHistory(String sessionId, RedissonClient redissonClient) {
		RedisChatMessageRepository redisChatMessageRepository = new RedisChatMessageRepository(redissonClient);
		dataBaseChatMessageHistory = new DataBaseChatMessageHistory(sessionId, redisChatMessageRepository);
	}

	@Override
	public void addMessage(BaseMessage message) {
		dataBaseChatMessageHistory.addMessage(message);
	}

	@Override
	public void clear() {
		dataBaseChatMessageHistory.clear();
	}

	@Override
	public List<BaseMessage> getMessages() {
		return dataBaseChatMessageHistory.getMessages();
	}
}
