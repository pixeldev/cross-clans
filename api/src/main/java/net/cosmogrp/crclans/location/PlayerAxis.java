package net.cosmogrp.crclans.location;

import net.cosmogrp.storage.mongo.DocumentBuilder;
import net.cosmogrp.storage.mongo.DocumentCodec;
import net.cosmogrp.storage.mongo.DocumentReader;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PlayerAxis implements DocumentCodec {

    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    private PlayerAxis(
            String worldName,
            double x, double y, double z,
            float yaw, float pitch
    ) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location toLocation() {
        return new Location(
                getWorld(),
                x, y, z,
                yaw, pitch
        );
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAxis that = (PlayerAxis) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Float.compare(that.yaw, yaw) == 0 &&
                Float.compare(that.pitch, pitch) == 0 &&
                Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "PlayerAxis{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    public static PlayerAxis fromLocation(Location location) {
        return new PlayerAxis(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public static PlayerAxis exactly(Player player) {
        return fromLocation(player.getLocation());
    }

    public static PlayerAxis centered(Player player) {
        Location location = player.getLocation();

        return new PlayerAxis(
                player.getWorld().getName(),
                location.getBlockX() + 0.5,
                location.getBlockY(),
                location.getBlockZ() + 0.5,
                location.getYaw(),
                location.getPitch()
        );
    }

    public static PlayerAxis fromDocument(DocumentReader reader) {
        return new PlayerAxis(
                reader.readString("world"),
                reader.readDouble("x"),
                reader.readDouble("y"),
                reader.readDouble("z"),
                reader.readFloat("yaw"),
                reader.readFloat("pitch")
        );
    }

    @Override
    public Document toDocument() {
        return DocumentBuilder.create()
                .write("world", worldName)
                .write("x", x)
                .write("y", y)
                .write("z", z)
                .write("yaw", yaw)
                .write("pitch", pitch)
                .build();
    }
}
