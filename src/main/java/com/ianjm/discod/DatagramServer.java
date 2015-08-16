package com.ianjm.discod;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Function;

public class DatagramServer {
    private final int port;
    private final Function<byte[], byte[]> handler;

    private boolean shutdown = false;

    public DatagramServer(int port, Function<byte[], byte[]> handler) {
        this.port = port;
        this.handler = handler;
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public void serve() throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket(port);
        while (!shutdown) {
            byte[] rec = new byte[1024];
            DatagramPacket recPacket = new DatagramPacket(rec, rec.length);
            socket.receive(recPacket);
            byte [] out = handler.apply(recPacket.getData());
            if (out != null) {
                DatagramPacket outPacket = new DatagramPacket(out, out.length, recPacket.getAddress(), recPacket.getPort());
                socket.send(outPacket);
            }
        }
    }
}
