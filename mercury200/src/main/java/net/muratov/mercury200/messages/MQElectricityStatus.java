package net.muratov.mercury200.messages;


import net.muratov.mq.MQRequest;

import java.util.UUID;

public class MQElectricityStatus extends MQRequest {
    public Long Timestamp;
    public Double Voltage;
    public Double Amperage;
    public Double Power;
    public MQElectricityStatus( Double voltage, Double amperage, Double power ){
        super(UUID.randomUUID(), "net.muratov.electricity.status", null);
        this.Timestamp = System.currentTimeMillis();
        this.Voltage = voltage;
        this.Amperage = amperage;
        this.Power = power;
    }

    public MQElectricityStatus(){

    }
}
