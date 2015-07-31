package org.qty.aws.lambda.pojo;

import java.util.ArrayList;
import java.util.List;

public class Command {

    private String path;
    private List<String> args = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

}
