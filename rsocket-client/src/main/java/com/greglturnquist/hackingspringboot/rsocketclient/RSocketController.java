package com.greglturnquist.hackingspringboot.rsocketclient;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.*;

@RestController
public class RSocketController {

    private final Mono<RSocketRequester> requester;

    public RSocketController(RSocketRequester.Builder builder) {
        this.requester = builder
                .dataMimeType(APPLICATION_JSON)
                .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
                .connectTcp("localhost", 7000)
                .retry(5)
                .cache();
                //.rsocketConnector(c -> c.reconnect(Retry.fixedDelay(5, Duration.ofSeconds(2))))
                //.tcp("localhost", 7000);

    }

    @PostMapping("/items/request-response") // <1>
    Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
        return this.requester //
                .flatMap(rSocketRequester -> rSocketRequester //
                        .route("newItems.request-response") // <2>
                        .data(item) // <3>
                        .retrieveMono(Item.class)) // <4>
                .map(savedItem -> ResponseEntity.created( // <5>
                        URI.create("/items/request-response")).body(savedItem));
    }

    @GetMapping(value = "/items/request-stream", produces = MediaType.APPLICATION_NDJSON_VALUE) // <1>
    Flux<Item> findItemsUsingRSocketRequestStream() {
        return this.requester //
                .flatMapMany(rSocketRequester -> rSocketRequester // <2>
                        .route("newItems.request-stream") // <3>
                        .retrieveFlux(Item.class) // <4>
                        .delayElements(Duration.ofSeconds(1))); // <5>
    }

    @PostMapping("/items/fire-and-forget")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketFireAndForget(@RequestBody Item item) {
        return this.requester //
                .flatMap(rSocketRequester -> rSocketRequester //
                        .route("newItems.fire-and-forget") // <1>
                        .data(item) //
                        .send()) // <2>
                .then( // <3>
                        Mono.just( //
                                ResponseEntity.created( //
                                        URI.create("/items/fire-and-forget")).build()));
    }

    @GetMapping(value = "/items", produces = TEXT_EVENT_STREAM_VALUE) // <1>
    Flux<Item> liveUpdates() {
        return this.requester //
                .flatMapMany(rSocketRequester -> rSocketRequester //
                        .route("newItems.monitor") // <2>
                        .retrieveFlux(Item.class)); // <3>
    }
}
