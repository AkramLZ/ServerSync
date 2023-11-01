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

package me.akraml.serversync;

import lombok.Getter;
import me.akraml.serversync.broker.MessageBrokerService;
import me.akraml.serversync.server.ServersManager;

/**
 * The main class for ServerSync, which serves as a singleton instance to coordinate
 * the servers management and message brokering services.
 */
@Getter
public class ServerSync {

    private static ServerSync INSTANCE;

    private final ServersManager serversManager;
    private final MessageBrokerService messageBrokerService;

    /**
     * Private constructor to prevent instantiation from outside and enforce the singleton pattern.
     *
     * @param serversManager The servers manager to manage server operations.
     * @param messageBrokerService The message broker service to handle messaging.
     */
    private ServerSync(final ServersManager serversManager,
                       final MessageBrokerService messageBrokerService) {
        this.serversManager = serversManager;
        this.messageBrokerService = messageBrokerService;
    }

    /**
     * Retrieves the singleton instance of ServerSync.
     *
     * @return The singleton instance of ServerSync.
     */
    public static ServerSync getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the singleton instance of ServerSync. If the instance is already
     * initialized, this method will throw a {@link RuntimeException} to prevent
     * re-initialization.
     *
     * @param serversManager The servers manager for server operations.
     * @param messageBrokerService The message broker service for messaging.
     * @throws RuntimeException if an instance is already initialized.
     */
    public static void initializeInstance(final ServersManager serversManager,
                                          final MessageBrokerService messageBrokerService) {
        if (INSTANCE != null) throw new RuntimeException("Instance is already initialized");
        INSTANCE = new ServerSync(serversManager, messageBrokerService);
    }

}
