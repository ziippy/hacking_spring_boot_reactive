package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
public class HomeControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemRepository repository;

    @Test
    @WithMockUser(username = "alice", roles = { "SOME_OTHER_ROLE" }) // <1>
    void addingInventoryWithoutProperRoleFails() {
        this.webTestClient.post().uri("/") // <2>
                .exchange() // <3>
                .expectStatus().isForbidden(); // <4>
    }

    @Test
    @WithMockUser(username = "bob", roles = { "INVENTORY" }) // <1>
    void addingInventoryWithProperRoleSucceeds() {
        this.webTestClient //
                .post().uri("/") //
                .contentType(MediaType.APPLICATION_JSON) // <2>
                .bodyValue("{" + // <3>
                        "\"name\": \"iPhone 11\", " + //
                        "\"description\": \"upgrade\", " + //
                        "\"price\": 999.99" + //
                        "}") //
                .exchange() //
                .expectStatus().isOk(); // <4>

        this.repository.findByName("iPhone 11") // <5>
                .as(StepVerifier::create) // <6>
                .expectNextMatches(item -> { // <7>
                    assertThat(item.getDescription()).isEqualTo("upgrade");
                    assertThat(item.getPrice()).isEqualTo(999.99);
                    return true; // <8>
                }) //
                .verifyComplete(); // <9>
    }
}
