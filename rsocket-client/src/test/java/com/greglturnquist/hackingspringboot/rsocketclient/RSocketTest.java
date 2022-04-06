package com.greglturnquist.hackingspringboot.rsocketclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
public class RSocketTest {

    @Autowired WebTestClient webTestClient;
    @Autowired ItemRepository repository;

    @Test
    void verifyRemoteOperationsThroughRSocketRequestResponse() //
            throws InterruptedException {

        // Clean out the database
        this.repository.deleteAll() // <1>
                .as(StepVerifier::create) //
                .verifyComplete();

        // Create a new "item"
        this.webTestClient.post().uri("/items/request-response") // <2>
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
                .exchange() //
                .expectStatus().isCreated() // <3>
                .expectBody(Item.class) //
                .value(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                });

        Thread.sleep(500); // <4>

        // Verify the "item" has been added to MongoDB
        this.repository.findAll() // <4>
                .as(StepVerifier::create) //
                .expectNextMatches(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                    return true;
                }) //
                .verifyComplete();
    }

    @Test
    void verifyRemoteOperationsThroughRSocketRequestStream() //
            throws InterruptedException {
        // Clean out the database
        this.repository.deleteAll().block(); // <1>

        // Create 3 new "item"s
        List<Item> items = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> new Item("name - " + i, "description - " + i, i)) // <2>
                .collect(Collectors.toList());

        this.repository.saveAll(items).blockLast(); // <3>


        // Get stream
        this.webTestClient.get().uri("/items/request-stream")
                .accept(MediaType.APPLICATION_NDJSON) // <4>
                .exchange() //
                .expectStatus().isOk()
                .returnResult(Item.class) // <5>
                .getResponseBody() // <6>
                .as(StepVerifier::create)
                .expectNextMatches(itemPredicate("1")) // <7>
                .expectNextMatches(itemPredicate("2"))
                .expectNextMatches(itemPredicate("3"))
                .verifyComplete(); // <8>
    }

    private Predicate<Item> itemPredicate(String num) {
        return item -> {
            assertThat(item.getName()).startsWith("name");
            assertThat(item.getName()).endsWith(num);
            assertThat(item.getDescription()).startsWith("description");
            assertThat(item.getDescription()).endsWith(num);
            assertThat(item.getPrice()).isPositive();
            return true;
        };
    }

    @Test
    void verifyRemoteOperationsThroughRSocketFireAndForget() throws InterruptedException {

        // Clean out the database
        this.repository.deleteAll() // <1>
                .as(StepVerifier::create) //
                .verifyComplete();

        // Create a new "item"
        this.webTestClient.post().uri("/items/fire-and-forget") // <2>
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
                .exchange() //
                .expectStatus().isCreated() // <3>
                .expectBody().isEmpty(); // <4>

        Thread.sleep(500); //

        // Verify the "item" has been added to MongoDB
        this.repository.findAll() // <5>
                .as(StepVerifier::create) //
                .expectNextMatches(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                    return true;
                }) //
                .verifyComplete();
    }
}
