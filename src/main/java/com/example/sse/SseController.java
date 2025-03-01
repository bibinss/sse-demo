package com.example.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalTime;

@RestController
@RequestMapping("/sse")
public class SseController {
    private final Sinks.Many<String> sink;

    public SseController() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    private void startEventGenerator() {
        Flux.interval(Duration.ofSeconds(2))
                .map(i -> "Update at " + LocalTime.now())
                .subscribe(sink::tryEmitNext);
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        return Flux.interval(Duration.ofSeconds(2))  // Emit every 2 seconds
                .map(sequence -> "Event " + sequence + " at " + LocalTime.now());
    }
}
