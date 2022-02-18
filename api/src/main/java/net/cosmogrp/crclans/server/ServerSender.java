package net.cosmogrp.crclans.server;

import net.cosmogrp.crclans.location.PlayerAxis;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ServerSender {

    default void teleport(
            Player player, String serverName,
            PlayerAxis playerAxis
    ) {
        teleport(player, ServerLocation.create(serverName, playerAxis));
    }

    default void teleport(
            Player player, String serverName,
            Location location
    ) {
        teleport(player, serverName, PlayerAxis.fromLocation(location));
    }

    void teleport(Player player, ServerLocation serverLocation);

    void checkTeleport(Player player);

}
