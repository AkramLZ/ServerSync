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

import com.google.gson.JsonObject;
import me.akraml.serversync.ServersManager;
import me.akraml.serversync.server.ServerImpl;
import me.akraml.serversync.server.ServerMessageType;

import java.util.function.Consumer;

/**
 * Abstracts the handling of messages related to servers.
 * This class provides a basic structure to receive and interpret messages,
 * and act upon them using a {@link ServersManager}. The specifics of how the messages are
 * received and sent to this handler should be implemented in derived classes.
 *
 * @version 1.0-SNAPSHOT
 */
public abstract class MessageBroker {

    /** The servers manager used to perform actions based on received messages. */
    private final ServersManager serversManager;

    /**
     * Constructs a new MessageBroker with the given {@link ServersManager}.
     *
     * @param serversManager The servers manager to use for actions on servers.
     */
    public MessageBroker(final ServersManager serversManager) {
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
                // What should we published during server creation?
                // Only server name, ip and port
                case CREATE: {
                    final String ip = jsonObject.get("ip").getAsString();
                    final int port = jsonObject.get("port").getAsInt();
                    final ServerImpl server = new ServerImpl(name, ip, port);

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
                    // TODO Handle updating
                    break;
                }
                case HEARTBEAT: {
                    // TODO Handle heartbeat
                    break;
                }
                case REMOVE: {
                    // TODO Handle removal
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    /**
     * Abstract method that, when implemented, should start the mechanism
     * to begin listening for and handling messages.
     */
    public abstract void startHandler();

    /**
     * Represents the state of a player update action.
     * This enum is used to indicate whether a player is being added to
     * or removed from a server during an update message process.
     */
    private enum PlayerUpdateState {

        /** Indicates that a player is being added to the server. */
        ADD,

        /** Indicates that a player is being removed from the server. */
        REMOVE
    }

}
