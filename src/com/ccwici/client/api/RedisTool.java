package com.ccwici.client.api;

import com.ccwici.client.connection.Connection;
import com.ccwici.client.protocol.Protocol;

import java.io.IOException;

public class RedisTool {
    private Connection connection;

    public RedisTool(String ip, int port, String password) throws IOException {
        this.connection = new Connection(ip, port, password);
        if(!this.connection.login()) {
            throw new IOException("登陆失败，可能是密码错误");
        }
    }

    public boolean set(String key, String value) {
        boolean success = false;
        connection.writeMessage(Protocol.buildSetCommand(key, value));
        String result = connection.readResponse().get();
        return checkOK(result);
    }

    public String get(String key) {
        connection.writeMessage(Protocol.buildGetCommand(key));
        String result = connection.readResponse().get();
        return result.equals(Connection.KEY_NOTEXISTS) ? null : result.split(Protocol.CRLF, 2)[1];
    }

    private boolean checkOK(String result) {
        return Connection.OK.equals(result);
    }

    public static void main(String[] args) throws IOException {
        RedisTool tool = new RedisTool("ip", 9852, "password");
        if(tool.set("mykey", "mmmmmmmmmmmmmm")) {
            System.out.println("成功");
        }
        System.out.println(tool.get("mykey"));
    }
}
