package com.greglturnquist.hackingspringboot.rsocketserver;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.awt.*;
import java.util.Date;

@Data
public class Item {
   private @Id String id;
   private String name;
   private String description;
   private double price;
   private String distributorRegion;
   private Date releaseDate;
   private int availableUnits;
   private Point location;
   private boolean active;

   private Item() {}

   public Item(String name, double price) {
      this.name = name;
      this.price = price;
   }

   public Item(String name, String description, double price) {
      this.name = name;
      this.description = description;
      this.price = price;
   }

   public Item(String id, String name, String description, double price) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
   }
}
