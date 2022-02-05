package net.cosmogrp.crclans.clan;

import net.cosmogrp.storage.model.AbstractModel;
import net.cosmogrp.storage.sql.identity.DataType;
import net.cosmogrp.storage.sql.identity.SQLConstraint;
import net.cosmogrp.storage.sql.meta.SQLTable;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SQLTable(
        name = "clans",
        elements = {
                @SQLTable.Element(
                        column = "id",
                        type = DataType.STRING,
                        constraints = {SQLConstraint.PRIMARY, SQLConstraint.NOT_NULL}
                ),
                @SQLTable.Element(
                        column = "name",
                        type = DataType.STRING
                ),
                @SQLTable.Element(
                        column = "ownerId",
                        type = DataType.UUID
                )
        }
)
public class Clan extends AbstractModel {

    private final UUID ownerId;
    private final String name;
    private final Set<UUID> members;

    public Clan(String tag, String name, Player owner) {
        super(tag);
        this.name = name;
        this.ownerId = owner.getUniqueId();
        this.members = new HashSet<>();
    }

    public Clan(
            String id, UUID ownerId,
            String name,
            Set<UUID> members
    ) {
        super(id);
        this.ownerId = ownerId;
        this.name = name;
        this.members = members;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Clan{" +
                "ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}
