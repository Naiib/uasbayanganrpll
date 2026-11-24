package com.example.boardgamebuddy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.nio.charset.Charset;

@SpringBootTest(properties = {
        "spring.ai.openai.api-key=test",
        "spring.ai.openai.base-url=http://localhost:8099"
})
public class SpringAiBoardGameServiceWireMockTests {

    @Value("classpath:/test-openai-response.json")
    Resource responseResource;

    @Autowired
    ChatClient.Builder chatClientBuilder;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setup() throws IOException {
        var cannedResponse = responseResource.getContentAsString(Charset.defaultCharset());
        mockWebServer = new MockWebServer();
        mockWebServer.start(8099);
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(cannedResponse));
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testAskQuestion() {
        var boardGameService = new SpringAiBoardGameService(chatClientBuilder);
        var answer = boardGameService.askQuestion(
                new Question("What is the capital of France?"));
        Assertions.assertThat(answer).isNotNull();
        Assertions.assertThat(answer.answer()).isEqualTo("Paris");
    }
}