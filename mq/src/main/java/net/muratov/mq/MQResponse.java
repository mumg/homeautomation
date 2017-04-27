package net.muratov.mq;


import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class MQResponse extends MQMessage {
    @SerializedName("messageId")
    public UUID messageId;
    @SerializedName("resultCode")
    public Integer resultCode;
    public MQResponse(MQRequest request, String name, Integer resultCode){
        super(request.transactionId, name, RESPONSE);
        this.messageId = request.messageId;
        this.resultCode = resultCode;
    }

    public MQResponse(UUID transactionId, UUID messageId, String name, Integer resultCode){
        super(transactionId, name, RESPONSE);
        this.messageId = messageId;
        this.resultCode = resultCode;
    }

    public MQResponse(){

    }
}
