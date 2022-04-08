package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@SpringBootTest
@EnableHypermediaSupport(type = HAL)
@AutoConfigureWebTestClient
public class ApiItemControllerTest {

    @Autowired
    WebTestClient webTestClient; // <2>

    @Autowired ItemRepository repository;

    @Autowired
    HypermediaWebTestClientConfigurer webClientConfigurer;

    @Test
    @WithMockUser(username = "alice", roles = { "SOME_OTHER_ROLE" }) // <1>
    void addingInventoryWithoutProperRoleFails() {
        this.webTestClient //
                .post().uri("/api/items/add") // <2>
                .contentType(MediaType.APPLICATION_JSON) //
                .bodyValue("{" + //
                        "\"name\": \"iPhone X\", " + //
                        "\"description\": \"upgrade\", " + //
                        "\"price\": 999.99" + //
                        "}") //
                .exchange() //
                .expectStatus().isForbidden(); // <3>
    }

    @Test
    @WithMockUser(username = "bob", roles = { "INVENTORY" }) // <1>
    void addingInventoryWithProperRoleSucceeds() {
        this.webTestClient //
                .post().uri("/api/items/add") // <2>
                .contentType(MediaType.APPLICATION_JSON) //
                .bodyValue("{" + //
                        "\"name\": \"iPhone X\", " + //
                        "\"description\": \"upgrade\", " + //
                        "\"price\": 999.99" + //
                        "}") //
                .exchange() //
                .expectStatus().isCreated(); // <3>

        this.repository.findByName("iPhone X") // <4>
                .as(StepVerifier::create) //
                .expectNextMatches(item -> { //
                    assertThat(item.getDescription()).isEqualTo("upgrade");
                    assertThat(item.getPrice()).isEqualTo(999.99);
                    return true; //
                }) //
                .verifyComplete(); //
    }

}
