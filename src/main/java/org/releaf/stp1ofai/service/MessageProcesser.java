package org.releaf.stp1ofai.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class MessageProcesser {

    public Flux<String> processMessage(String input){
        // 模拟用户输入并生成回复
        String[] words = input.split(" ");
        return Flux.fromArray(words)
                .delayElements(Duration.ofMillis(500));
    }
}
