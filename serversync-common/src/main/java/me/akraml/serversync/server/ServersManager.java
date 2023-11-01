/*
 * MIT License
 *
 * Copyright (c) 2023 Akram Louze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.akraml.serversync.server;

import java.time.Duration;
import java.util.*;

/**
 * Manages a collection of servers and provides utility methods for server management tasks.
 * This class also manages a heartbeat task to periodically check for server activity and
 * removes any server that hasn't sent a heartbeat signal within a specified time frame.
 *
 * @version 1.0-BETA
 */
public abstract class ServersManager {

    /** Timer to schedule and manage the heartbeat task. */
    private final Timer timer =  new Timer();

    /** Map storing the servers using their names as the key. */
    private final Map<String, ServerImpl> servers = new HashMap<>();

    /** Integers for heartbeat task delay and maximum time to remove the server. */
    protected int heartbeatSchedulerDelay, maxAliveTime;

    /**
     * Starts a recurring task to check servers for their heartbeat signal.
     * Servers that haven't sent a heartbeat signal within the last 30 seconds will be removed.
     */
    public final void startHeartbeatTask() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final List<ServerImpl> toRemove =  new ArrayList<>();
                servers.values().forEach(server -> {
                    if (System.currentTimeMillis() - server.getLastHeartbeat() > maxAliveTime) {
                        toRemove.add(server);
                    }
                });
                toRemove.forEach(ServersManager.this::removeServer);
                toRemove.clear();
            }
        }, 0L, Duration.ofSeconds(heartbeatSchedulerDelay).toMillis());
    }

    /**
     * Retrieves a server instance by its name.
     *
     * @param name The name of the server.
     * @return The server instance or null if not found.
     */
    public final Server getServer(String name) {
        return this.servers.get(name);
    }

    /**
     * Adds a server to the managed collection of servers.
     *
     * @param server The server to be added.
     */
    public final void addServer(Server server) {
        this.servers.put(server.getName(), (ServerImpl) server);
        registerInProxy(server);
    }

    /**
     * Removes a server from the managed collection of servers.
     * Also, triggers an unregister action specific to the proxy.
     *
     * @param server The server to be removed.
     */
    public final void removeServer(Server server) {
        unregisterFromProxy(server);
        this.servers.remove(server.getName());
    }

    /**
     * Abstract method that should be implemented to unregister a server from the associated proxy.
     *
     * @param server The server to be unregistered from the proxy.
     */
    protected abstract void unregisterFromProxy(Server server);

    /**
     * Abstract method that should be implemented to register a server in the proxy server.
     *
     * @param server The server to be registered in the proxy.
     */
    protected abstract void registerInProxy(Server server);
}
