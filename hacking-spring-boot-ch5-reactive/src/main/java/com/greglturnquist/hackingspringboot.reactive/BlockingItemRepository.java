package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.repository.CrudRepository;

public interface BlockingItemRepository extends CrudRepository<Item, String> {

}
