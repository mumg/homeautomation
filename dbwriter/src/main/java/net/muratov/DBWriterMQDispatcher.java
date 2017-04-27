package net.muratov;


import net.muratov.messages.MQElectricityConsumption;
import net.muratov.messages.MQElectricityStatus;
import net.muratov.mq.MQDispatcher;
import net.muratov.mq.MQRequest;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component()
@Scope(value = "singleton")
@EnableRabbit
public class DBWriterMQDispatcher extends MQDispatcher {


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate;


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
        this.registerRequest("net.muratov.electricity.status", MQElectricityStatus.class, new RequestListener() {
            @Override
            public void OnRequest(String id, MQRequest request) {
                MQElectricityStatus status = (MQElectricityStatus)request;
                jdbcTemplate.update("insert into electricity_status (timestamp, voltage, amperage, power) VALUES(" +
                                "FROM_UNIXTIME(?), ?, ?, ?)",
                        new Object[] {status.Timestamp / 1000, status.Voltage, status.Amperage, status.Power});
            }
        });

        this.registerRequest("net.muratov.electricity.consumption", MQElectricityConsumption.class, new RequestListener() {
            @Override
            public void OnRequest(String id, MQRequest request) {
                MQElectricityConsumption consumption = (MQElectricityConsumption) request;
                jdbcTemplate.update("insert into electricity_consumption (timestamp, tariff, value) VALUES(\n" +
                                "\tFROM_UNIXTIME(?),\n" +
                                "    ?,\n" +
                                "    ?\n" +
                                ")",
                        new Object[] {consumption.Timestamp / 1000, consumption.Tariff, consumption.Value});
            }
        });
    }

}
