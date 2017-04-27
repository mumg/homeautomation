package net.muratov.messages;

import com.google.gson.annotations.SerializedName;
import net.muratov.mq.MQRequest;

public class MQElectricityConsumption extends MQRequest {
    @SerializedName("Timestamp")
    public Long Timestamp;
    @SerializedName("Tariff")
    public Integer Tariff;
    @SerializedName("Value")
    public Double Value;
    public MQElectricityConsumption(){

    }
}
