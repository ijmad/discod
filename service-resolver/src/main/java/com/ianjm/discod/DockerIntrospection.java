package com.ianjm.discod;

import com.spotify.docker.client.*;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

import javax.ws.rs.NotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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
        try {
            ContainerInfo container = DOCKER.inspectContainer(containerName);
            if (container != null) {
                if (container.state().running()) {
                    return toInetAddress(container.networkSettings().ipAddress());
                }
            }
        }
        catch (ContainerNotFoundException e) {
            // drop through
        }

        return Optional.empty();
    }

    public static Optional<InetAddress> findIpByImage(String imageName) throws DockerException, InterruptedException {
        final Predicate<Container> filter;
        if (imageName.contains(":")) {
            filter = c -> c.image().equals(imageName);
        }
        else {
            filter = c -> c.image().equals(imageName) || c.image().startsWith(imageName + ":");
        }

        List<Container> containers = DOCKER.listContainers().stream().filter(filter).collect(toList());
        if (containers.isEmpty()) {
            return Optional.empty();
        }
        else {
            // get random - balance load
            return findIpByContainer(containers.get(RANDOM.nextInt(containers.size())).names().get(0));
        }
    }
}
