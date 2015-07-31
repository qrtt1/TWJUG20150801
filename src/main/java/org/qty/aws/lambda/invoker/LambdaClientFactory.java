package org.qty.aws.lambda.invoker;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;

public class LambdaClientFactory {

    public static AWSLambda create() {
        ProfileCredentialsProvider provider = new ProfileCredentialsProvider("qty");

        AWSLambda client = new AWSLambdaClient(provider);
        client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

        return client;
    }
}
