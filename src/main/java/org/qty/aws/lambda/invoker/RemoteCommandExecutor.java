package org.qty.aws.lambda.invoker;

import org.qty.aws.lambda.pojo.Command;
import org.qty.aws.lambda.pojo.Result;

import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface RemoteCommandExecutor {

    @LambdaFunction(functionName = "HelloLambda")
    public Result execute(Command command);

}