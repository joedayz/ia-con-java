package com.example.boardgamebuddy;

import com.example.boardgamebuddy.gamedata.GameRepository;
import com.example.boardgamebuddy.gamedata.GameTools;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.resolution.SpringBeanToolCallbackResolver;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.stringtemplate.v4.ST;

import java.nio.charset.Charset;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = {
    AiConfig.class,
    GameTools.class,
    SpringAiBoardGameService.class,
    BoardGameService.class})
@Disabled("For now...need to rethink this test and how it's designed")
public class SpringAiBoardGameServiceMockOpenAiTests {

    @MockitoBean
    GameRepository gameRepository;

    @MockitoBean
    VectorStore vectorStore;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    SpringAiBoardGameService service;

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public ChatClient.Builder chatClientBuilder(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder,
                                                    ToolCallingManager toolCallingManager) {
            var openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(
                    OpenAiApi.builder()
                        .baseUrl("https://api.openai.com")
                        .apiKey("TEST_API_KEY")
                        .restClientBuilder(restClientBuilder)
                        .webClientBuilder(webClientBuilder)
                        .build())
                .toolCallingManager(toolCallingManager)
                .build();
            return ChatClient.builder(openAiChatModel);
        }
    }

    @Test
    public void testStuff() throws Exception {
        var expectedAnswer = "Checkers is a game for two people.";
        var content = "{\\\"gameTitle\\\":\\\"Checkers\\\", \\\"answer\\\":\\\"" + expectedAnswer + "\\\"}";
        mockOpenAiChatResponse(content);
        var answer = service.askQuestion(new Question("Checkers","How many can play?"), "conversation-id-1");
        Assertions.assertThat(answer.answer()).isEqualTo(expectedAnswer);
    }

    private void mockOpenAiChatResponse(String content) throws Exception {
        var responseResource = new ClassPathResource("/response.json");
        var st = new ST(StreamUtils.copyToString(responseResource.getInputStream(), Charset.defaultCharset()), '$', '$');
        st = st.add("content", content);
        mockServer.expect(requestTo("https://api.openai.com/v1/chat/completions"))
            .andRespond(withSuccess(st.render(), MediaType.APPLICATION_JSON));
    }

    @TestConfiguration
    public static class TestConfig2 {
        @Bean
        ToolCallbackResolver toolCallbackResolver(GenericApplicationContext applicationContext) {
            return SpringBeanToolCallbackResolver.builder()
                .applicationContext(applicationContext)
                .build();
        }
    }

}