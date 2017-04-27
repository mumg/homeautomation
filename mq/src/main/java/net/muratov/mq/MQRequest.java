package net.muratov.mq;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class MQRequest extends MQMessage{
    @SerializedName("responseQueue")
    public String responseQueue;
    @SerializedName("messageId")
    public UUID messageId;
    public MQRequest(UUID transactionID, String name, String responseQueue){
        super(transactionID, name, REQUEST);
        this.messageId = UUID.randomUUID();
        this.responseQueue = responseQueue;
    }

    public MQRequest(){

    }

}
