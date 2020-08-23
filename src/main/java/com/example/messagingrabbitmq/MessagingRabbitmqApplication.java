package com.example.messagingrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MessagingRabbitmqApplication {

    //Exchanges
    static final String directExchangeName = "spring-boot-exchange-direct";
    static final String topicExchangeName = "spring-boot-exchange-topic";
    static final String fanoutExchangeName = "spring-boot-exchange-fanout";

    //Queues
    static final String queueName = "spring-boot-queue";
    static final String queueNamePrimary = "spring-boot-queue-primary";
    static final String queueNameSecondary = "spring-boot-queue-secundary";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    Queue queueP() {
        return new Queue(queueNamePrimary, false);
    }

    @Bean
    Queue queueS() {
        return new Queue(queueNameSecondary, false);
    }

    //DIRECT
    @Bean
    DirectExchange exchangeD() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    Binding bindingD(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with("rk");
    }

    //TOPIC
    @Bean
    TopicExchange exchangeT() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding bindingT(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    //FANOUT
    @Bean
    FanoutExchange exchangeF() {
        return new FanoutExchange(fanoutExchangeName);
    }

    @Bean
    Binding bindingP(FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueP()).to(fanoutExchange);
    }

    @Bean
    Binding bindingS(FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueS()).to(fanoutExchange);
    }

    //
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName, queueNamePrimary, queueNameSecondary);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    public static void main(String[] args) {
        SpringApplication.run(MessagingRabbitmqApplication.class, args);
    }

}
