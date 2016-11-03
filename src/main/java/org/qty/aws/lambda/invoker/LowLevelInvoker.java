package org.qty.aws.lambda.invoker;

import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LowLevelInvoker {

    public static void main(String[] args) {

        JsonObject object = new JsonObject();
        object.addProperty("path", "/bin/ls");

        JsonArray list = new JsonArray();
        list.add("-lsR");
        object.add("args", list);

        String payload = object.toString();

        byte[] data = LambdaClientFactory.create()
                .invoke(new InvokeRequest().withFunctionName("HelloLambda").withPayload(payload)).getPayload().array();

        System.out.println(new JsonParser().parse(new String(data)).getAsJsonObject().get("output"));
    }
}
