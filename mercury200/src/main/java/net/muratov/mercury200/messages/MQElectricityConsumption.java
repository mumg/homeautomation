package net.muratov.mercury200.messages;

import net.muratov.mq.MQRequest;

import java.util.UUID;

public class MQElectricityConsumption extends MQRequest {
    public Long Timestamp;
    public Integer Tariff;
    public Double Value;
    public MQElectricityConsumption( Integer tariff, Double value ){
        super(UUID.randomUUID(), "net.muratov.electricity.consumption", null);
        this.Timestamp = System.currentTimeMillis();
        this.Tariff = tariff;
        this.Value = value;
    }

    public MQElectricityConsumption(){

    }
}
