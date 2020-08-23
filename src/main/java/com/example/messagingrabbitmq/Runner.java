package com.example.messagingrabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending first message...");
        rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.directExchangeName, "rk", "Hello from RabbitMQ! DIRECT");
        Thread.sleep(1000);

        System.out.println("Sending second message...");
        Thread.sleep(5000);
        rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ! TOPIC");
        Thread.sleep(1000);

        System.out.println("Sending third message...");
        Thread.sleep(5000);
        rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.fanoutExchangeName, "", "Hello from RabbitMQ! FANOUT");

        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}