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

package me.akraml.serversync.bungee;

import me.akraml.serversync.server.Server;
import me.akraml.serversync.server.ServersManager;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

/**
 * Implementation of the {@link ServersManager} for a BungeeCord proxy environment.
 * This manager handles the registration and un-registration of server instances specifically
 * for BungeeCord.
 */
public final class BungeeServersManager extends ServersManager {

    private final BungeeServerSyncPlugin plugin;

    public BungeeServersManager(final BungeeServerSyncPlugin plugin) {
        this.plugin = plugin;
        this.heartbeatSchedulerDelay = plugin.getConfig().getInt("heartbeat-scheduler-delay");
        this.maxAliveTime = plugin.getConfig().getInt("max-alive-time");
    }

    /**
     * Unregisters a server from the BungeeCord proxy. This removes the server from the list of
     * servers known to the proxy, effectively making it inaccessible to players through the proxy.
     *
     * @param server The server to unregister from the proxy.
     */
    @Override
    protected void unregisterFromProxy(Server server) {
        plugin.getProxy().getServers().remove(server.getName());
    }

    /**
     * Registers a server in the BungeeCord proxy. This adds the server to the list of servers
     * known to the proxy, allowing players to access it through the proxy. If the server is
     * already registered, it does nothing to avoid duplication.
     *
     * @param server The server to register with the proxy.
     */
    @Override
    protected void registerInProxy(Server server) {
        if (plugin.getProxy().getServers().get(server.getName()) == null) {
            final ServerInfo serverInfo = plugin.getProxy().constructServerInfo(
                    server.getName(),
                    InetSocketAddress.createUnresolved(server.getIp(), server.getPort()),
                    "",
                    false
            );
            plugin.getProxy().getServers().put(server.getName(), serverInfo);
        }
    }
}
