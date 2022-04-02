package com.greglturnquist.hackingspringboot.reactive;

import lombok.Data;
import lombok.NonNull;

@Data
public class CartItem {
    @NonNull private Item item;
    private int quantity = 1;

    private CartItem() {};

    public CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
    }

    public CartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public void increment() {
        this.quantity++;
    }

    public void decrement() {
        this.quantity--;
    }
}
