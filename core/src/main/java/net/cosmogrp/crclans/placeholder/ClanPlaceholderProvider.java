package net.cosmogrp.crclans.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.data.ClanData;
import net.cosmogrp.crclans.clan.data.ClanDataService;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyData;
import net.cosmogrp.crclans.clan.enemy.ClanEnemyService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.UserService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

public class ClanPlaceholderProvider extends PlaceholderExpansion
        implements Relational {

    @Inject private UserService userService;
    @Inject private ClanMemberService memberService;
    @Inject private ClanDataService dataService;
    @Inject private ClanAllyService allyService;
    @Inject private ClanEnemyService enemyService;
    @Inject private MessageHandler messageHandler;

    @Override
    public @NotNull String getIdentifier() {
        return "clan";
    }

    @Override
    public @NotNull String getAuthor() {
        return "CosmoGroup Development";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(
            OfflinePlayer offlinePlayer,
            @NotNull String placeholder
    ) {
        if (!(offlinePlayer instanceof Player player)) {
            return "";
        }

        User user = userService.getUser(player.getUniqueId());

        if (user == null) {
            return "";
        }

        if (placeholder.equals("hasclan")) {
            return user.hasClan() ? "true" : "false";
        }

        String tag = user.getClanTag();

        if (tag == null) {
            return messageHandler.get(player, "placeholder.no-clan");
        }

        switch (placeholder) {
            case "tag":
            case "name":
            case "id":
                return user.getClanTag();
            case "owner": {
                ClanMemberData memberData = memberService.getData(tag);

                if (memberData == null) {
                    return messageHandler.get(player, "placeholder.no-clan");
                }

                return memberData.getOwner().getPlayerName();
            }
            case "creation": {
                ClanData clanData = dataService.getData(tag);

                if (clanData == null) {
                    return messageHandler.get(player, "placeholder.no-clan");
                }

                new SimpleDateFormat(messageHandler.get(player, "placeholder.time-format"))
                        .format(clanData.getCreation());
            }
            default:
                return null;
        }
    }

    @Override
    public String onPlaceholderRequest(Player one, Player two, String identifier) {
        if (one == null || two == null) {
            return "";
        }

        User userOne = userService.getUser(one.getUniqueId());
        User userTwo = userService.getUser(two.getUniqueId());

        if (userOne == null || userTwo == null) {
            return "";
        }

        String tagOne = userOne.getClanTag();
        String tagTwo = userTwo.getClanTag();

        if (tagOne == null || tagTwo == null) {
            return "";
        }

        String relationPath = "placeholder.relation.";

        if (tagOne.equals(tagTwo)) {
            relationPath += "member";
        } else {
            ClanAllyData allyData = allyService.getData(tagOne);

            if (allyData == null) {
                relationPath += "neutral";
            } else {
                if (allyData.contains(tagTwo)) {
                    relationPath += "ally";
                } else {
                    ClanEnemyData enemyData = enemyService.getData(tagOne);

                    if (enemyData != null) {
                        if (enemyData.contains(tagTwo)) {
                            relationPath += "enemy";
                        } else {
                            relationPath += "neutral";
                        }
                    }
                }
            }
        }

        String color = messageHandler.get(one, relationPath + ".color");
        String text = messageHandler.get(one, relationPath + ".text");

        return switch (identifier) {
            case "full" -> color + text;
            case "color" -> color;
            case "text" -> text;
            default -> null;
        };
    }
}
