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

package me.akraml.serversync.broker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.akraml.serversync.player.SyncPlayer;
import me.akraml.serversync.server.Server;
import me.akraml.serversync.server.ServersManager;
import me.akraml.serversync.server.ServerImpl;
import me.akraml.serversync.server.ServerMessageType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Abstracts the handling of messages related to servers.
 * This class provides a basic structure to receive and interpret messages,
 * and act upon them using a {@link ServersManager}. The specifics of how the messages are
 * received and sent to this handler should be implemented in derived classes.
 *
 * @version 1.0-BETA
 */
public abstract class MessageBrokerService {

    /** The servers manager used to perform actions based on received messages. */
    private final ServersManager serversManager;

    /**
     * Constructs a new MessageBroker with the given {@link ServersManager}.
     *
     * @param serversManager The servers manager to use for actions on servers.
     */
    public MessageBrokerService(final ServersManager serversManager) {
        this.serversManager = serversManager;
    }

    /**
     * Handles the reception of a server-related message encapsulated in a {@link JsonObject}.
     * Depending on the message type, different actions are taken, such as creating, updating,
     * sending a heartbeat, or removing a server.
     *
     * @param jsonObject The JSON object containing the message data.
     */
    public void onMessageReceive(final JsonObject jsonObject) {
        try {
            final ServerMessageType messageType = ServerMessageType.valueOf(jsonObject.get("type").getAsString());
            final String name = jsonObject.get("name").getAsString();
            switch (messageType) {
                // This will handle server creation message.
                // What should we publish during server creation?
                // Only server name, ip and port
                case CREATE: {
                    final String ip = jsonObject.get("ip").getAsString();
                    final int port = jsonObject.get("port").getAsInt(),
                            maxPlayers = jsonObject.get("maxPlayers").getAsInt();
                    final ServerImpl server = (ServerImpl) Server.of(name, ip, port);
                    server.setMaxPlayers(maxPlayers);

                    if (serversManager.getServer(name) != null) {
                        serversManager.removeServer(server);
                    }
                    serversManager.addServer(server);
                    break;
                }

                /*
                 * (This documentation might be removed, as the developer of this software is talking to himself)
                 * What do we need to update?
                 * - Online players (UUID;Username)
                 * - Max players (Integer)
                 *
                 * How to do it?
                 * to keep it lightweight (we don't want to fuck the cpu up ofc), for online players updating there will be ADD & REMOVE enum.
                 * If it's add, then add player in the server.
                 * If it's remove, then remove player from the server.
                 * For max players, easy just update the integer
                 */
                case UPDATE: {
                    final ServerImpl server = (ServerImpl) serversManager.getServer(name);
                    // If server is not present, then wait till the heartbeat.
                    if (server == null) break;
                    final JsonElement playerUpdateElement = jsonObject.get("playerUpdate");
                    // If it's not null, then it means we need to update a player state.
                    if (playerUpdateElement != null) {
                        final PlayerUpdateState updateState = PlayerUpdateState.valueOf(playerUpdateElement.getAsString());
                        final String[] playerData = jsonObject.get("playerToUpdate").getAsString().split(";");
                        final UUID uuid = UUID.fromString(playerData[0]);
                        final String playerName = playerData[1];
                        switch (updateState) {
                            case ADD: {
                                final SyncPlayer syncPlayer = new SyncPlayer(uuid, playerName);
                                if (!server.containsPlayer(uuid)) {
                                    server.addPlayer(syncPlayer);
                                }
                                break;
                            }
                            case REMOVE: {
                                final SyncPlayer syncPlayer = server.getPlayer(uuid);
                                if (syncPlayer != null) {
                                    server.removePlayer(syncPlayer);
                                }
                                break;
                            }
                        }
                        break;
                    }
                    // Now, we need to perform a check for max players update.
                    final JsonElement maxPlayersElement = jsonObject.get("maxPlayers");
                    if (maxPlayersElement != null) {
                        server.setMaxPlayers(maxPlayersElement.getAsInt());
                        break;
                    }

                    break;
                }
                case HEARTBEAT: {
                    ServerImpl server = (ServerImpl) serversManager.getServer(name);
                    if (server != null) {
                        server.heartbeat();
                    } else {
                        // It means the server is not registered yet, so we need to register it.
                        final String ip = jsonObject.get("ip").getAsString();
                        final int port = jsonObject.get("port").getAsInt();
                        // Initialize a new instance and register it.
                        server = (ServerImpl) Server.of(name, ip, port);
                        serversManager.addServer(server);
                        // Update max players value.
                        server.setMaxPlayers(jsonObject.get("maxPlayers").getAsInt());
                        // Add players in the server.
                        final JsonArray playersArray = jsonObject.getAsJsonArray("players");
                        for (final JsonElement element : playersArray) {
                            final String[] playerData = element.getAsString().split(";");
                            final UUID uuid = UUID.fromString(playerData[0]);
                            final String playerName = playerData[1];
                            final SyncPlayer syncPlayer = new SyncPlayer(uuid, playerName);
                            server.addPlayer(syncPlayer);
                        }
                    }
                    break;
                }
                case REMOVE: {
                    final Server server = serversManager.getServer(name);
                    if (server != null) {
                        serversManager.removeServer(server);
                    }
                    break;
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public void publishPlayerUpdate(final String serverName,
                                    final SyncPlayer syncPlayer,
                                    final PlayerUpdateState updateState) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", ServerMessageType.UPDATE.name());
        jsonObject.addProperty("name", serverName);
        jsonObject.addProperty("playerUpdate", updateState.name());
        jsonObject.addProperty("playerToUpdate", syncPlayer.getUuid() + ";" + syncPlayer.getUsername());
        publish(jsonObject);
    }

    /**
     * Publishes an update for the maximum number of players allowed on a specific server.
     * This method constructs a JSON message that updates the maximum player count for the server
     * with the given name.
     *
     * @param serverName The name of the server to update.
     * @param maxPlayers The new maximum number of players that can join the server.
     */

    public void publishMaxPlayersUpdate(final String serverName,
                                        final int maxPlayers) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", ServerMessageType.UPDATE.name());
        jsonObject.addProperty("name", serverName);
        jsonObject.addProperty("maxPlayers", maxPlayers);
        publish(jsonObject);
    }

    /**
     * Publishes a heartbeat message for a server. The heartbeat contains current data about
     * the server including its name, IP, port, maximum players, and a list of currently
     * connected players. This is typically used to show that the server is active and to update
     * its current state.
     *
     * @param serverName The name of the server.
     * @param ip         The IP address of the server.
     * @param port       The port on which the server is running.
     * @param maxPlayers The maximum number of players that can join the server.
     * @param players    A collection of the current players on the server.
     */
    public void publishHeartbeat(final String serverName,
                                 final String ip,
                                 final int port,
                                 final int maxPlayers,
                                 final Collection<SyncPlayer> players) {
        final JsonObject jsonObject = new JsonObject();
        final JsonArray playersArray = new JsonArray();
        players.forEach(syncPlayer -> playersArray.add(syncPlayer.getUuid() + ";" + syncPlayer.getUsername()));
        jsonObject.addProperty("type", ServerMessageType.HEARTBEAT.name());
        jsonObject.addProperty("name", serverName);
        jsonObject.addProperty("ip", ip);
        jsonObject.addProperty("port", port);
        jsonObject.addProperty("maxPlayers", maxPlayers);
        jsonObject.add("players", playersArray);
        publish(jsonObject);
    }

    /**
     * Publishes a message to create a new server entry. This message contains the necessary
     * information such as the server's name, IP, port, and the maximum number of players allowed.
     * This can be used when a new server is being added to the network.
     *
     * @param serverName The name of the server to create.
     * @param ip         The IP address of the server.
     * @param port       The port on which the server is running.
     * @param maxPlayers The maximum number of players that can join the server.
     */
    public void publishCreate(final String serverName,
                              final String ip,
                              final int port,
                              final int maxPlayers) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", serverName);
        jsonObject.addProperty("ip", ip);
        jsonObject.addProperty("port", port);
        jsonObject.addProperty("maxPlayers", maxPlayers);
        publish(jsonObject);
    }

    /**
     * Publishes a message to remove a server from the network. This message instructs that
     * the server with the given name should be removed, typically because it is no longer
     * operational or has been decommissioned.
     *
     * @param serverName The name of the server to be removed.
     */
    public void publishRemove(final String serverName) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", ServerMessageType.REMOVE.name());
        jsonObject.addProperty("name", serverName);
        publish(jsonObject);
    }

    /**
     * Abstract method that, when implemented, should start the mechanism
     * to begin listening for and handling messages.
     */
    public abstract void startHandler();

    /**
     * Stops and shut-downs the message broker client.
     */
    public abstract void stop();

    /**
     * Publishes a message into the current message broker channel.
     *
     * @param message Message to publish.
     */
    public abstract void publish(final JsonObject message);

    /**
     * Represents the state of a player update action.
     * This enum is used to indicate whether a player is being added to
     * or removed from a server during an update message process.
     */
    public enum PlayerUpdateState {

        /** Indicates that a player is being added to the server. */
        ADD,

        /** Indicates that a player is being removed from the server. */
        REMOVE
    }

}
