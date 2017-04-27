package net.muratov.mq;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class MQDispatcher {

    Logger logger = Logger.getLogger("mq");

    private Gson mGson;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private MQMessageDeserializer mDeserializer;

    private Map< String , RequestListener > mRequestListeners = new HashMap<>();

    private Map < String , ResponseListener > mResponseListeners = new HashMap<>();


    public void init(){
        mDeserializer = new MQMessageDeserializer();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(MQMessage.class, mDeserializer);
        mGson = builder.create();
    }


    public interface RequestListener{
        void OnRequest( String id, MQRequest request );
    }

    public interface ResponseListener{
        void OnResponse(String id, MQResponse response );
    }

    public void registerRequest (String request, Class<? extends MQRequest> messageClass, RequestListener listener ){
        mDeserializer.registerRequest(request, messageClass);
        mRequestListeners.put(request, listener);
    }

    public void registerResponse(String response, Class <? extends  MQResponse> messageClass, ResponseListener listener ){
        mDeserializer.registerResponse(response, messageClass);
        mResponseListeners.put(response, listener);
    }

    public void process(String message) {
        try {
            logger.info("received  " + message);
            MQMessage msg = mGson.fromJson(message, MQMessage.class);
            if ( msg.type == MQMessage.REQUEST){
                RequestListener listener = mRequestListeners.get(msg.name);
                if ( listener != null ){
                    listener.OnRequest(msg.name, (MQRequest)msg);
                }
            }else if ( msg.type == MQMessage.RESPONSE){
                ResponseListener listener = mResponseListeners.get(msg.name);
                if ( listener != null ){
                    listener.OnResponse(msg.name, (MQResponse)msg);
                }
            }
        }catch (Exception e){
            logger.info("process: " + e.getMessage());
        }
    }


    public void request(String queueName, MQRequest request){
        try {
            String message = mGson.toJson(request);
            logger.info("request ["+queueName+"] " + message);
            rabbitTemplate.convertAndSend(queueName, message);
        }catch (Exception e){
            logger.info("request: " + e.getMessage());
        }
    }

    public void respond(String queueName, MQResponse response){
        try {
            String message = mGson.toJson(response);
            logger.info("response ["+queueName+"] " + message);
            rabbitTemplate.convertAndSend(queueName, message);
        }catch (Exception e){
            logger.info("respond: " + e.getMessage());
        }
    }

    public abstract String getQueueName();
}
