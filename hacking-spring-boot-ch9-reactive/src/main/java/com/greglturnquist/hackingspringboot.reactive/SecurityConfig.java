package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;


@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository repository) {
//        return username -> repository.findByName(username)
//                .map(user -> User.withDefaultPasswordEncoder()
//                        .username(user.getName())
//                        .password(user.getPassword())
//                        .authrities(user.getRoles().toArray(new String[0]))
//                        .build()
//                );
        return username -> repository.findByName(username)
                .map(user -> User.builder()
                        .username(user.getName())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[0]))
                        .build()
                );
    }

    static String role(String auth) {
        return "ROLE_" + auth;
    }

    @Bean
    CommandLineRunner userLoader(MongoOperations operations) {
        return args -> {
            operations.save(new com.greglturnquist.hackingspringboot.reactive.User( //
                    "greg", "{noop}password", Arrays.asList(role(USER))));

            operations.save(new com.greglturnquist.hackingspringboot.reactive.User( //
                    "manager", "{noop}password", Arrays.asList(role(USER), role(INVENTORY))));
        };
    }

    static final String USER = "USER";
    static final String INVENTORY = "INVENTORY";

//    @Bean
//    SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) { // <1>
//        return http //
//                .authorizeExchange(exchanges -> exchanges //
//                        //.pathMatchers(HttpMethod.POST, "/").hasRole(INVENTORY) // @EnableReactiveMethodSecurity 사용으로 인한 주석 처리
//                        //.pathMatchers(HttpMethod.DELETE, "/**").hasRole(INVENTORY) // @EnableReactiveMethodSecurity 사용으로 인한 주석 처리
//                        .anyExchange().authenticated() // <3>
//                        .and() //
//                        .httpBasic() // <4>
//                        .and() //
//                        .formLogin()) // <5>
//                .csrf().disable() //
//                .build();
//    }
}
