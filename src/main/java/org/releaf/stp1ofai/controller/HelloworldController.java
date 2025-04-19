package org.releaf.stp1ofai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/helloworld")
public class HelloworldController {

    private static final String DEFAULT_PROMPT = "你是一个古代人，请你根据用户的输入进行回答";

    private final ChatClient chatClient;

    public HelloworldController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(DEFAULT_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
    }

    @GetMapping("/simple/chat")
    public String simpleChat(@RequestParam String query){
        return chatClient.prompt(query).call().content();
    }

    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam String query, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(query).stream().content();
    }
}
