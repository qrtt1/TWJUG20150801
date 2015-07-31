package org.qty.aws.lambda.invoker;

import java.util.Arrays;

import org.qty.aws.lambda.pojo.Command;
import org.qty.aws.lambda.pojo.Result;

import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.util.json.JSONException;

public class ServiceProxyInvoker {

    public static void main(String[] args) throws JSONException {

        RemoteCommandExecutor executor = LambdaInvokerFactory.build(RemoteCommandExecutor.class,
                LambdaClientFactory.create());

        Command command = new Command();
        command.setPath("/bin/ls");
        command.setArgs(Arrays.asList("-lsR"));
        Result result = executor.execute(command);

        System.out.println(result.getOutput());

    }
}
