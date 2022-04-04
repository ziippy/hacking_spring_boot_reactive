package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemUnitTest {

    @Test
    void itemBasicsShouldWork() {
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);

        // AssertJ 를 사용한 값 일치 테스트
        assertThat(sampleItem.getId()).isEqualTo("item1");
        assertThat(sampleItem.getName()).isEqualTo("TV tray");
        assertThat(sampleItem.getDescription()).isEqualTo("Alf TV tray");
        assertThat(sampleItem.getPrice()).isEqualTo(19.99);

        Item sampleItem2 = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        assertThat(sampleItem).isEqualTo(sampleItem2);
    }
}
