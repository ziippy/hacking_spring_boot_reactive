package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class KitchenService {
    private List<Dish> menu = Arrays.asList(
            new Dish("Sesame chicken"),
            new Dish("Lo mein noodles, plain"),
            new Dish("Sweet & sour beef")
    );

    private Random picker = new Random();

    private Dish randomDish() {
        return menu.get(picker.nextInt(menu.size()));
    }

    /*
    요리 스트림 생성
     */
    Flux<Dish> getDishes() {
        return Flux.<Dish> generate(sink -> sink.next(randomDish())).delayElements(Duration.ofMillis(250));
    }
}
