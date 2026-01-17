package com.kafka.kafkademo.consumers;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {


    @KafkaListener(topics = "mysql-server.testdb.users" , groupId = "user-group")
    public void consume(String message) {
        System.out.println("User Consumer: " + message);
    }
}
