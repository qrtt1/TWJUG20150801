package org.qty.aws.lambda.invoker;

import java.util.Arrays;

import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class LowLevelInvoker {

    public static void main(String[] args) throws JSONException {

        String payload = new JSONObject()
            .put("path", "/bin/ls")
            .put("args", Arrays.asList("-lsR")).toString();

        byte[] data = LambdaClientFactory.create().invoke(new InvokeRequest()
            .withFunctionName("HelloLambda")
            .withPayload(payload)).getPayload().array();

        System.out.println(new JSONObject(new String(data)).get("output"));
    }
}
