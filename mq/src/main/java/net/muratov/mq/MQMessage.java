package net.muratov.mq;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

public class MQMessage implements Serializable {
    public static final int REQUEST = 0;
    public static final int RESPONSE = 1;

    @SerializedName("type")
    public int type;
    @SerializedName("name")
    public String name;
    @SerializedName("transactionId")
    public UUID transactionId;
    public MQMessage(UUID transactionId, String name, int type){
        this.transactionId = transactionId;
        this.name = name;
        this.type = type;
    }
    public MQMessage(){

    }
}
