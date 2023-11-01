package me.akraml.serversync.bungee;

import lombok.RequiredArgsConstructor;
import me.akraml.serversync.server.Server;
import me.akraml.serversync.server.ServersManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

/**
 * Implementation of the {@link ServersManager} for a BungeeCord proxy environment.
 * This manager handles the registration and un-registration of server instances specifically
 * for BungeeCord.
 */
@RequiredArgsConstructor
public class BungeeServersManager extends ServersManager {

    private final ProxyServer proxyServer;

    /**
     * Unregisters a server from the BungeeCord proxy. This removes the server from the list of
     * servers known to the proxy, effectively making it inaccessible to players through the proxy.
     *
     * @param server The server to unregister from the proxy.
     */
    @Override
    protected void unregisterFromProxy(Server server) {
        proxyServer.getServers().remove(server.getName());
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
        if (proxyServer.getServers().get(server.getName()) == null) {
            final ServerInfo serverInfo = proxyServer.constructServerInfo(
                    server.getName(),
                    InetSocketAddress.createUnresolved(server.getIp(), server.getPort()),
                    "",
                    false
            );
            proxyServer.getServers().put(server.getName(), serverInfo);
        }
    }
}
