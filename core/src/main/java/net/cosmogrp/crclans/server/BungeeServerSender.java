package net.cosmogrp.crclans.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import io.papermc.lib.PaperLib;
import net.cosmogrp.crclans.location.PlayerAxis;
import net.cosmogrp.storage.redis.connection.RedisCache;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class BungeeServerSender implements ServerSender {

    @Inject private ServerData serverData;
    @Inject private Plugin plugin;
    @Inject private RedisCache redisCache;
    @Inject private Gson gson;

    @Override
    public void teleport(Player player, ServerLocation serverLocation) {
        // check if location is in same server
        String targetServer = serverLocation.getServerName();
        if (targetServer.equals(serverData.getBungeeIdentifier())) {
            PaperLib.teleportAsync(player, serverLocation.getPlayerAxis().toLocation());
        } else {
            // send to other server
            @SuppressWarnings("UnstableApiUsage")
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("Connect");
            output.writeUTF(targetServer);

            player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
            redisCache.set(
                    "teleports", player.getUniqueId().toString(),
                    gson.toJson(serverLocation.getPlayerAxis())
            );
        }
    }

    @Override
    public void checkTeleport(Player player) {
        String playerId = player.getUniqueId().toString();
        String teleport = redisCache.get("teleports", playerId);

        if (teleport != null) {
            PlayerAxis location = gson.fromJson(teleport, PlayerAxis.class);
            PaperLib.teleportAsync(player, location.toLocation());
            redisCache.del("teleports", playerId);
        }
    }
}
