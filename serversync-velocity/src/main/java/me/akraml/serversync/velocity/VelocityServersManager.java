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

package me.akraml.serversync.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.RequiredArgsConstructor;
import me.akraml.serversync.server.Server;
import me.akraml.serversync.server.ServersManager;

import java.util.Optional;

/**
 * Manages server registrations and un-registrations on a Velocity-based proxy.
 * Extends the {@link ServersManager} to use the functionalities provided by the Velocity API,
 * applying the necessary actions to the proxy server when a server is added or removed.
 */
@RequiredArgsConstructor
public final class VelocityServersManager extends ServersManager {

    private final ProxyServer proxyServer;

    /**
     * Unregisters a server from the proxy server.
     * If the server specified by the {@link Server} object is currently registered
     * with the proxy, it is unregistered, effectively removing it from the network's server list.
     *
     * @param server The server to unregister from the proxy.
     */
    @Override
    protected void unregisterFromProxy(Server server) {
        final Optional<RegisteredServer> optional = proxyServer.getServer(server.getName());
        if (optional.isEmpty()) return;
        final ServerInfo serverInfo = optional.get().getServerInfo();
        proxyServer.unregisterServer(serverInfo);
    }
}
