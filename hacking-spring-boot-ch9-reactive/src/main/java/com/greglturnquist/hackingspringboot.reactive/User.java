package com.greglturnquist.hackingspringboot.reactive;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class User {

    private @Id
    String id;
    private String name;
    private String password;
    private List<String> roles;

    private User() {}

    public User(String id, String name, String password, List<String> roles) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public User(String name, String password, List<String> roles) {
        this.name = name;
        this.password = password;
        this.roles = roles;
    }
}
