package org.qty.aws.lambda.invoker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.qty.aws.lambda.pojo.Command;
import org.qty.aws.lambda.pojo.Result;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.util.json.JSONException;

public class PeepLambdaEnvironment {

    public static void main(String[] args) throws JSONException, IOException {
        RemoteCommandExecutor executor = buildExecutor();

        launchAndSave(executor, "001-list-files.txt", "/bin/ls", "-lsR");
        launchAndSave(executor, "002-export-env-vars.txt", "/bin/bash", "-c",
                "whoami; echo; uname -a; echo; export; echo; locale; echo; ulimit -a");
        launchAndSave(executor, "003-process-list.txt", "/bin/ps", "aux");
        launchAndSave(executor, "004-java.txt", "/bin/bash", "-c", "java -version; whereis java");

    }

    private static RemoteCommandExecutor buildExecutor() {
        AWSLambda client = LambdaClientFactory.create();
        client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        RemoteCommandExecutor executor = LambdaInvokerFactory.build(RemoteCommandExecutor.class, client);
        return executor;
    }

    private static void launchAndSave(RemoteCommandExecutor executor, String filename, String cmd, String... args)
            throws IOException {
        Command command = new Command();
        command.setPath(cmd);
        command.setArgs(Arrays.asList(args));
        Result result = executor.execute(command);

        FileUtils.write(new File(filename), result.getOutput());
    }
}