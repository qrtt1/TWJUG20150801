package org.qty.aws.lambda;

import org.qty.aws.lambda.pojo.Command;
import org.qty.aws.lambda.pojo.Result;
import org.qty.aws.lambda.util.ApplicationExecutor;

import com.amazonaws.services.lambda.runtime.Context;

public class HelloLambda {

    public Result handler(Command command, Context context) throws Exception {

        ApplicationExecutor executor = new ApplicationExecutor(
                command.getPath(), 
                command.getArgs().toArray(new String[0]));
        
        Result result = new Result();
        result.setOutput(executor.execute(context.getRemainingTimeInMillis()));

        return result;
    }

}
