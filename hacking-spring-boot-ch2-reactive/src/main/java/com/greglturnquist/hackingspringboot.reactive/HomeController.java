package com.greglturnquist.hackingspringboot.reactive;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;
    @Autowired
    private InventoryService inventoryService;

//    public HomeController(ItemRepository itemRepository, CartRepository cartRepository, CartService cartService) {
//        this.itemRepository = itemRepository;
//        this.cartRepository = cartRepository;
//        this.cartService = cartService;
//    }

//    @GetMapping
//    Mono<String> home() {
//        return Mono.just("home");
//    }

    @GetMapping
    Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
                            .modelAttribute("items", this.itemRepository.findAll())
                            .modelAttribute("cart", this.cartRepository.findById("My Cart").defaultIfEmpty(new Cart("My Cart")))
                            .build());
    }

    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id) {
        return this.cartService.addToCart("My Cart", id)
                .thenReturn("redirect:/");
//        return this.cartRepository.findById("My Cart")
//                .defaultIfEmpty(new Cart("My Cart"))
//                .flatMap(cart -> cart.getCartItems().stream()
//                                    .filter(cartItem -> cartItem.getItem().getId().equals(id))
//                                    .findAny()
//                                    .map(cartItem -> {
//                                        cartItem.increment();
//                                        return Mono.just(cart);
//                                    })
//                                    .orElseGet(() -> {
//                                        return this.itemRepository.findById(id)
//                                                .map(item -> new CartItem(item))
//                                                .map(cartItem -> {
//                                                    cart.getCartItems().add(cartItem);
//                                                    return cart;
//                                                });
//                                    }))
//                .flatMap(cart -> this.cartRepository.save(cart))
//                .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    Mono<Rendering> search(@RequestParam(required = false) String name,
                           @RequestParam(required = false) String description,
                           @RequestParam boolean useAnd) {
        return Mono.just(Rendering.view("home.html")
                        .modelAttribute("items", this.inventoryService.searchByExample(name, description, useAnd))
                        .modelAttribute("cart", this.cartRepository.findById("My Cart").defaultIfEmpty(new Cart("My Cart")))
                        .build());
    }
}
