package net.muratov.mq;


import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MQMessageDeserializer implements JsonDeserializer<MQMessage>
{
    private Gson mGson;
    private GsonBuilder mGsonBuilder;
    private Map<String, Class<? extends MQRequest>>  mRequestRegistry;
    private Map<String, Class<? extends MQResponse>> mResponseRegistry;

    class UUIDDeserializer implements JsonDeserializer<UUID>
    {
        @Override
        public UUID deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException
        {
            return UUID.fromString(je.getAsString());
        }
    }


    public MQMessageDeserializer()
    {
        mGsonBuilder = new GsonBuilder();
        mGsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        mGsonBuilder.registerTypeAdapter(UUID.class, new UUIDDeserializer());
        mGson = mGsonBuilder.create();
        mRequestRegistry = new HashMap<>();
        mResponseRegistry = new HashMap<>();
    }

    public void registerTypeAdapter(Class <?> instanceClass, JsonDeserializer deserializer ){
        mGsonBuilder.registerTypeAdapter(instanceClass, deserializer);
    }

    public void registerRequest(String request, Class<? extends MQRequest> messageInstanceClass)
    {
        mRequestRegistry.put(request, messageInstanceClass);
    }

    public void registerResponse(String request, Class<? extends MQResponse> messageInstanceClass)
    {
        mResponseRegistry.put(request, messageInstanceClass);
    }

    @Override
    public MQMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        try
        {
            JsonObject messageObject = json.getAsJsonObject();
            JsonElement type = messageObject.get("type");
            JsonElement name = messageObject.get("name");
            if ( type.getAsInt() == MQMessage.REQUEST){
                Class<? extends MQRequest> commandInstanceClass = mRequestRegistry.get(name.getAsString());
                MQRequest request = mGson.fromJson(json, commandInstanceClass);
                return request;
            }else if ( type.getAsInt() == MQMessage.RESPONSE){
                Class<? extends MQResponse> commandInstanceClass = mResponseRegistry.get(name.getAsString());
                MQResponse response = mGson.fromJson(json, commandInstanceClass);
                return response;
            }else{
                throw new RuntimeException("invalid message type");
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}