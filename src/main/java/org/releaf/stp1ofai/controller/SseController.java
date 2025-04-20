package org.releaf.stp1ofai.controller;

import org.releaf.stp1ofai.service.MessageProcesser;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class SseController {

    private final MessageProcesser messageProcesser;

    public SseController(MessageProcesser messageProcesser) {
        this.messageProcesser = messageProcesser;
    }


    /**
     * new things
     * @param message
     * @return
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestParam String message){
        return messageProcesser.processMessage(message)
                .map(word -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .event("message")
                        .data(word)
                        .build());
    }

    /**
     * old days
     * @return
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(sequence))
                        .event("message")
                        .data("Hello World " + sequence)
                        .build());

    }
}
