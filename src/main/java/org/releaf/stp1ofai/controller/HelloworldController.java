package org.releaf.stp1ofai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

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


    @GetMapping("/advisor/chat/{id}")
    public Flux<String> advisorChat(
            HttpServletResponse response,
            @PathVariable String id,
            @RequestParam String query ) {
        response.setCharacterEncoding("UTF-8");

        return this.chatClient.prompt(query)
                .advisors(
                        advisor -> advisor
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY,id)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY,100)
                ).stream().content();
    }
}
