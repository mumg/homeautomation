package net.muratov.messages;


import com.google.gson.annotations.SerializedName;
import net.muratov.mq.MQRequest;

import java.util.UUID;

public class MQElectricityStatus extends MQRequest {
    @SerializedName("Timestamp")
    public Long Timestamp;
    @SerializedName("Voltage")
    public Double Voltage;
    @SerializedName("Amperage")
    public Double Amperage;
    @SerializedName("Power")
    public Double Power;

    public MQElectricityStatus(){

    }
}
