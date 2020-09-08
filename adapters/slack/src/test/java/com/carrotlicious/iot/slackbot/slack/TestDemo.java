package com.carrotlicious.iot.slackbot.slack;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Objects;

public class TestDemo {

    UserRepository userRepository = new UserRepository();


    @Test
    public void testGetUserById() {
        StepVerifier.create(this.getUserById(1))
                .assertNext(user -> {
                    System.out.println("Result: " + user);
                    Assert.assertEquals(new User(1, "Jason"), user);
                })
                .expectComplete()
                .verify();
    }

    protected Mono<User> getUserById(Integer id) {
        return Mono.just(id)
                .filter(Objects::nonNull)
                .flatMap(userRepository::findById)
                .switchIfEmpty(Mono.error(new Exception("User not found.")))
                .doOnSuccess(this::mask);
    }

    protected Mono<User> getUserByIdv2(Integer id) {
        return Mono.just(id)
                .filter(Objects::nonNull)
                .flatMap(userRepository::findById)
                .subscribeOn(Schedulers.elastic())
                .switchIfEmpty(Mono.error(new Exception("User not found.")))
                .doOnSuccess(this::mask);
    }

    private void mask(User user) {
        user.setPassword("");
    }

    class UserRepository {
        public Mono<User> findById(int id) {
            return (id == 1) ? Mono.just(new User(1, "Jason", "12345")) : Mono.empty();
        }
    }

    class User {
        private int userId;
        private String username;
        private String password;

        public User(int userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public User(int userId, String username, String password) {
            this.userId = userId;
            this.username = username;
            this.password = password;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return userId == user.userId &&
                    Objects.equals(username, user.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, username);
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }


}
