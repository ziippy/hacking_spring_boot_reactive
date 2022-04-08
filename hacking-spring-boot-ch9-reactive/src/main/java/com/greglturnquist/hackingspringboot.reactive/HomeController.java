package com.greglturnquist.hackingspringboot.reactive;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    private static String cartName(Authentication auth) {
        return auth.getName() + " Cart";
    }

    private static String cartNameOAuth(OAuth2User oAuth2User) {
        return oAuth2User.getName() + "'s Cart";
    }

//    @GetMapping
//    Mono<Rendering> home() {
//        return Mono.just(Rendering.view("home.html")
//                            .modelAttribute("items", this.itemRepository.findAll().doOnNext(System.out::println))
//                            .modelAttribute("cart", this.cartRepository.findById("My Cart").defaultIfEmpty(new Cart("My Cart")))
//                            .build());
//    }

//    @GetMapping
//    Mono<Rendering> home(Authentication auth) {
//        return Mono.just(Rendering.view("home.html")
//                .modelAttribute("items", this.inventoryService.getInventory())
//                .modelAttribute("cart", this.inventoryService.getCart(cartName(auth)).defaultIfEmpty(new Cart(cartName(auth))))
//                .modelAttribute("auth", auth)
//                .build());
//    }

    @GetMapping
    Mono<Rendering> home( //
                          @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                          @AuthenticationPrincipal OAuth2User oauth2User) { // <1>
        return Mono.just(Rendering.view("home.html") //
                .modelAttribute("items", this.inventoryService.getInventory()) //
                .modelAttribute("cart", this.inventoryService.getCart(cartNameOAuth(oauth2User)) // <2>
                        .defaultIfEmpty(new Cart(cartNameOAuth(oauth2User)))) //

                // 인증 상세 정보 조회는 조금 복잡
                .modelAttribute("userName", oauth2User.getName()) //
                .modelAttribute("authorities", oauth2User.getAuthorities()) //
                .modelAttribute("clientName", //
                        authorizedClient.getClientRegistration().getClientName()) //
                .modelAttribute("userAttributes", oauth2User.getAttributes()) //
                .build());
    }

    @PostMapping("/add/{id}")
    Mono<String> addToCart(Authentication auth, @PathVariable String id) {
        return this.inventoryService.addItemToCart(cartName(auth), id)
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

    @PostMapping("/remove/{id}")
    Mono<String> removeFromCart(Authentication auth, @PathVariable String id) {
        return this.inventoryService.removeOneFromCart(cartName(auth), id)
                .thenReturn("redirect:/");
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

    @PostMapping
    @ResponseBody
    Mono<Item> createItem(@RequestBody Item newItem) {
        return this.inventoryService.saveItem(newItem);
    }
}
