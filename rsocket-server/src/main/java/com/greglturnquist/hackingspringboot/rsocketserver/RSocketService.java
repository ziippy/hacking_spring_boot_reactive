package com.greglturnquist.hackingspringboot.rsocketserver;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Controller
public class RSocketService {

    private final ItemRepository repository;
    
    //private final EmitterProcessor<Item> itemProcessor; // deprecated.. 아래로 대체
    private final Sinks.Many<Item> itemsSink;

    public RSocketService(ItemRepository repository) {
        this.repository = repository;
        
        this.itemsSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @MessageMapping("newItems.request-response")    // 요청-응답
    public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item) {
        return this.repository.save(item)
                .doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem));
    }

    @MessageMapping("newItems.request-stream")      // 요청-스트림
    public Flux<Item> findItemsViaRSocketRequestStream() {
        return this.repository.findAll()
                .doOnNext(this.itemsSink::tryEmitNext);
    }

    @MessageMapping("newItems.fire-and-forget")     // 실행 후 망각
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item) {
        return this.repository.save(item)
				.doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem))
                .then();
    }

    @MessageMapping("newItems.monitor")             // 채널
    public Flux<Item> monitorNewItems() {
		return this.itemsSink.asFlux();
    }
}
