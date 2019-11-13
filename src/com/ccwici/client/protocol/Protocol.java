package com.ccwici.client.protocol;

public class Protocol {
    public static final String CRLF = "\r\n";

    public static String buildSetCommand(String key, String value) {
        return buildCommand(Command.SET, key, value);
    }

    public static String buildGetCommand(String key) {
        return buildCommand(Command.GET, key);
    }

    public static String buildCommand(Command command, String ...args) {
        int argCount = args.length + 1;
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(argCount).append(CRLF);
        sb.append("$").append(command.toString().length()).append(CRLF);
        sb.append(command.toString()).append(CRLF);
        for (String param : args) {
            sb.append("$").append(param.length()).append(CRLF);
            sb.append(param).append(CRLF);
        }
        return sb.toString();
    }


    public enum Command {
        SET, GET, DEL, AUTH
    }

    public static void main(String[] args) {
        System.out.println(Protocol.buildCommand(Command.SET, "mykey", "fdasf"));
    }

}
