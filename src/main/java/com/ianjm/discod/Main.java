package com.ianjm.discod;

import com.spotify.docker.client.DockerException;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String domain = "." + args[1] + ".";

        DNSResponder responder = new DNSResponder(
            (name) -> {
                if (name.endsWith(domain) && name.length() > domain.length()) {
                    Optional<InetAddress> ip = findDockerIpByName(name.substring(0, name.length() - domain.length()));
                    System.out.println(name + " -> " + ip);
                    return ip;
                }
                else {
                    return Optional.empty();
                }
            }
        );

        DatagramServer server = new DatagramServer(
            port,
            (rec) -> respond(responder, rec).orElse(null)
        );

        server.serve();
    }

    private static Optional<InetAddress> findDockerIpByName(String name) {
        try {
            return DockerIntrospection.findIpByContainer(name);
        }
        catch (DockerException | InterruptedException e) {
            return Optional.empty();
        }
    }

    private static Optional<byte[]> respond(DNSResponder responder, byte [] rec) {
        try {
            return Optional.of(responder.respond(new Message(rec)).toWire());
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }
}
