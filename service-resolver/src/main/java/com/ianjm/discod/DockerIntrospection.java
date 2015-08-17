package com.ianjm.discod;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Random;

public class DockerIntrospection {
    private static final DockerClient DOCKER;
    private static final Random RANDOM = new Random();

    static {
        try {
            DOCKER = DefaultDockerClient.fromEnv().build();
        }
        catch (DockerCertificateException dce) {
            throw new RuntimeException("Unable to build DockerClient", dce);
        }
    }

    private static Optional<InetAddress> toInetAddress(String address) {
        try {
            return Optional.of(InetAddress.getByName(address));
        }
        catch (UnknownHostException e) {
            return Optional.empty();
        }
    }

    /**
     * Find the IP for a running container, by friendly name or ID.
     */
    public static Optional<InetAddress> findIpByContainer(String containerName) throws DockerException, InterruptedException {
        ContainerInfo container = DOCKER.inspectContainer(containerName);

        if (container != null) {
            if (container.state().running()) {
                return toInetAddress(container.networkSettings().ipAddress());
            }
        }

        return Optional.empty();
    }
}
