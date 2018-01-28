/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plan.system.info.connection;

import com.djrapitops.plan.api.exceptions.connection.NoServersException;
import com.djrapitops.plan.api.exceptions.database.DBException;
import com.djrapitops.plan.system.database.databases.Database;
import com.djrapitops.plan.system.info.request.*;
import com.djrapitops.plan.system.info.server.Server;
import com.djrapitops.plan.system.info.server.ServerInfo;
import com.djrapitops.plan.system.settings.Settings;
import com.djrapitops.plan.system.settings.locale.Locale;
import com.djrapitops.plan.system.settings.locale.Msg;
import com.djrapitops.plan.system.webserver.WebServerSystem;
import com.djrapitops.plan.utilities.MiscUtils;
import com.djrapitops.plugin.api.TimeAmount;
import com.djrapitops.plugin.api.utility.log.Log;

import java.util.Optional;
import java.util.UUID;

/**
 * Connection system for Bukkit servers.
 *
 * @author Rsl1122
 */
public class BukkitConnectionSystem extends ConnectionSystem {

    private long latestServerMapRefresh;

    private Server mainServer;

    public BukkitConnectionSystem() {
        latestServerMapRefresh = 0;
    }

    private void refreshServerMap() {
        if (latestServerMapRefresh < MiscUtils.getTime() - TimeAmount.SECOND.ms() * 15L) {
            try {
                Database database = Database.getActive();
                Optional<Server> bungeeInformation = database.fetch().getBungeeInformation();
                bungeeInformation.ifPresent(server -> mainServer = server);
                bukkitServers = database.fetch().getBukkitServers();
                latestServerMapRefresh = MiscUtils.getTime();
            } catch (DBException e) {
                Log.toLog(this.getClass().getName(), e);
            }
        }
    }

    @Override
    protected Server selectServerForRequest(InfoRequest infoRequest) throws NoServersException {
        refreshServerMap();

        if (mainServer == null && bukkitServers.isEmpty()) {
            throw new NoServersException("Zero servers available to process requests.");
        }

        Server server = null;
        if (infoRequest instanceof CacheRequest) {
            server = mainServer;
        } else if (infoRequest instanceof GenerateAnalysisPageRequest) {
            UUID serverUUID = ((GenerateAnalysisPageRequest) infoRequest).getServerUUID();
            server = bukkitServers.get(serverUUID);
        } else if (infoRequest instanceof GenerateInspectPageRequest) {
            Optional<UUID> serverUUID = getServerWherePlayerIsOnline((GenerateInspectPageRequest) infoRequest);
            if (serverUUID.isPresent()) {
                server = bukkitServers.getOrDefault(serverUUID.get(), ServerInfo.getServer());
            }
        }
        if (server == null) {
            throw new NoServersException("Proper server is not available to process request: " + infoRequest.getClass().getSimpleName());
        }
        return server;
    }

    @Override
    public void sendWideInfoRequest(WideRequest infoRequest) throws NoServersException {
        if (bukkitServers.isEmpty()) {
            throw new NoServersException("No Servers available to make wide-request: " + infoRequest.getClass().getSimpleName());
        }
        for (Server server : bukkitServers.values()) {
            WebExceptionLogger.logIfOccurs(this.getClass(), () -> sendInfoRequest(infoRequest, server));
        }
    }

    @Override
    public boolean isServerAvailable() {
        return mainServer != null && Settings.BUNGEE_OVERRIDE_STANDALONE_MODE.isFalse();
    }

    @Override
    public String getMainAddress() {
        return isServerAvailable() ? mainServer.getWebAddress() : ServerInfo.getServer().getWebAddress();

    }

    @Override
    public void enable() {
        refreshServerMap();

        boolean usingBungeeWebServer = ConnectionSystem.getInstance().isServerAvailable();
        boolean usingAlternativeIP = Settings.SHOW_ALTERNATIVE_IP.isTrue();

        if (!usingAlternativeIP && ServerInfo.getServerProperties().getIp().isEmpty()) {
            Log.infoColor(Locale.get(Msg.ENABLE_NOTIFY_EMPTY_IP).toString());
        }
        if (usingBungeeWebServer && usingAlternativeIP) {
            String webServerAddress = WebServerSystem.getInstance().getWebServer().getAccessAddress();
            Log.info("Make sure that this address points to the Bukkit Server: " + webServerAddress);
        }
    }

    @Override
    public void disable() {

    }
}