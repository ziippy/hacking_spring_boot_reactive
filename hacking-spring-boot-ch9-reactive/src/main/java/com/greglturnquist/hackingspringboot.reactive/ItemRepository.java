package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveCrudRepository<Item, String>,
                                            ReactiveQueryByExampleExecutor<Item> {

    Mono<Item> findByName(String name);

    Flux<Item> findByNameContaining(String partialName);

//    @Query("{ 'name' : ?0, 'age' : ?1 }")
//    Flux<Item> findItemsForCustomMonthlyReport(String name, int age);
//
//    @Query(sort = "{ 'age' : -1 }")
//    Flux<Item> findSortedStuffForWeeklyReport();

//    Flux<Item> findByDescription(String description);
//    Flux<Item> findByDescriptionContaining(String partialDescription);
    Flux<Item> findByDescriptionContainingIgnoreCase(String partialDescription);
    Flux<Item> findByNameContainingAndDescriptionContaining(String partialName, String partialDescription);
    Flux<Item> findByNameContainingOrDescriptionContaining(String partialName, String partialDescription);

    // 요구사항에 따라 이렇게 점점 늘어날지도..

    // 이를 대체하기 위해 Query by Example 을 이용해보자.
}
