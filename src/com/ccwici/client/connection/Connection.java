package com.ccwici.client.connection;

import com.ccwici.client.protocol.Protocol;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public class Connection implements Closeable {
    public static final String OK = "+OK\r\n";
    public static final String KEY_NOTEXISTS = "$-1\r\n";
    String ip;
    int port;
    String password;
    SocketChannel socketChannel = SocketChannel.open();
    private byte[] array = new byte[1024];

    public Connection(String ip, int port, String password) throws IOException {
        this.ip = ip;
        this.port = port;
        this.password = password;
        socketChannel.configureBlocking(false);
        socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        socketChannel.connect(socketAddress);
    }

    public boolean login() {
        this.writeMessage(Protocol.buildCommand(Protocol.Command.AUTH, password));
        String result = this.readResponse().get();
        return OK.equals(result);
    }

    public boolean writeMessage(String cmd) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(cmd.getBytes());
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 执行读命令
     * @return 读到的消息字节数组
     */
    public Optional<String> readResponse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        byteBuffer.clear();
        int size = 0;
        try {
            size = socketChannel.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        if(size < 0) {
            return Optional.empty();
        }
        byteBuffer.flip();
        byte[] bs = new byte[size];
        byteBuffer.get(bs);
        return Optional.of(new String(bs));
    }


    @Override
    public void close() throws IOException {
        if(socketChannel != null && socketChannel.isConnected()) {
            socketChannel.close();
        }
    }

    public static void main(String[] args) {
        try(Connection connection = new Connection("ip", 1111, "password")) {
            connection.login();
            connection.writeMessage(Protocol.buildSetCommand("mykey","afjsjfl"));
            connection.readResponse().ifPresent((data)->{System.out.println(new String(data));});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
