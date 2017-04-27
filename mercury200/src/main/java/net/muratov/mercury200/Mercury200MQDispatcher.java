package net.muratov.mercury200;

import net.muratov.mq.MQDispatcher;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.logging.Logger;

@Component()
@Scope(value = "singleton")
@EnableRabbit

public class Mercury200MQDispatcher extends MQDispatcher {

    Logger logger = Logger.getLogger("mq.dispatcher");

    @Value("${MQ.queue}")
    public String queue;

    @Bean
    public Queue queue() {
        return new Queue(queue);
    }

    public String getQueueName(){
        return queue;
    }

    @RabbitListener(queues = "${MQ.queue}")
    public void process(String message) {
        super.process(message);
    }

    @PostConstruct
    public void init (){
        super.init();
    }



}
