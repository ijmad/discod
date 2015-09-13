package com.ianjm.discod;

import com.spotify.docker.client.DockerException;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String imageDomain = "." + args[1] + ".";
        String containerDomain = "." + args[2] + ".";

        DNSResponder responder = new DNSResponder(
            (name) -> {
                if (name.endsWith(imageDomain) && name.length() > imageDomain.length()) {
                    Optional<InetAddress> ip = findByImageName(name.substring(0, name.length() - imageDomain.length()));
                    System.out.println(name + " -> " + ip);
                    return ip;
                }
                else if (name.endsWith(containerDomain) && name.length() > containerDomain.length()) {
                    Optional<InetAddress> ip = findByContainerName(name.substring(0, name.length() - imageDomain.length()));
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

    private static Optional<InetAddress> findByContainerName(String containerName) {
        try {
            return DockerIntrospection.findIpByContainer(containerName);
        }
        catch (DockerException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<InetAddress> findByImageName(String imageName) {
        try {
            return DockerIntrospection.findIpByImage(imageName);
        }
        catch (DockerException | InterruptedException e) {
            e.printStackTrace();
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
