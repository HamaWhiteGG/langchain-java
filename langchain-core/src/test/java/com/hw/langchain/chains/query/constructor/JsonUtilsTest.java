package com.hw.langchain.chains.query.constructor;

import static org.junit.jupiter.api.Assertions.*;

import com.hw.langchain.schema.Document;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author zhangxiaojia002
 * @date 2023/7/20 8:52 下午
 **/
class JsonUtilsTest {

	@Test
	void jsonFormatDocuments(){
		Document document = new Document(
			"test document content", Map.of("fileName", "test.txt"));
		List<Document> documentList = Arrays.asList(document);
		String documentStr = documentList.toString();
		String documentJson = JsonUtils.toJsonStringWithIndent(documentList, 0);
		System.out.println("");
	}
}