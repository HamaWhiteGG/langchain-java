package com.hw.langchain.memory.chat.message.histories.database.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.memory.chat.message.histories.database.ChatMessageRepository;
import com.hw.langchain.schema.BaseMessage;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.experimental.Tolerate;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

/**
 * @author zhangxiaojia002
 * @date 2023/7/20 9:59 下午
 **/
@Builder
public class RedisChatMessageRepository implements ChatMessageRepository {

	private RedissonClient redissonClient;
	private String keyPrefix = "message_store";
	private Integer ttlSeconds;

	@Tolerate
	public RedisChatMessageRepository(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	@Tolerate
	public RedisChatMessageRepository(RedissonClient redissonClient, int ttlSeconds) {
		this.redissonClient = redissonClient;
		this.ttlSeconds = ttlSeconds;
	}

	/**
	 * Construct the record key to use
	 *
	 * @return
	 */
	private String key(String sessionId) {
		if (this.keyPrefix == null) {
			return sessionId;
		}
		return this.keyPrefix + sessionId;
	}

	@Override
	public List<BaseMessage> loadMessage(String sessionId) {
		RQueue<String> messageQueue = redissonClient.getQueue(key(sessionId));
		List<String> messageJSonStrList = messageQueue.readAll();
		return messageJSonStrList.stream().map(x -> {
			Map<String, Object> data =
				JsonUtils.convertFromJsonStr(x, new TypeReference<>() {
				});
			return BaseMessage.fromMap(data);
		}).toList();
	}

	@Override
	public void saveMessage(String sessionId, BaseMessage baseMessage) {
		RQueue<String> messageQueue = redissonClient.getQueue(key(sessionId));
		messageQueue.add(JsonUtils.toJsonStringWithIndent(baseMessage.toMap()));
		if (this.ttlSeconds != null) {
			messageQueue.expire(Duration.of(ttlSeconds, ChronoUnit.SECONDS));
		}
	}

	@Override
	public void clearSessionChatMessage(String sessionId) {
		RQueue<String> messageQueue = redissonClient.getQueue(key(sessionId));
		messageQueue.delete();
	}
}
