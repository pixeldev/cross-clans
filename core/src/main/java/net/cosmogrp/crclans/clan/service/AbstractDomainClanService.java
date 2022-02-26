package net.cosmogrp.crclans.clan.service;

import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.domain.Domain;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.storage.model.Model;
import net.cosmogrp.storage.mongo.DocumentCodec;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Function;

public abstract class AbstractDomainClanService
        <T extends Model & DocumentCodec & Domain>
        extends AbstractClanService<T>
        implements DomainClanService<T> {

    @Inject protected ClanMemberService memberService;

    private final String key;

    public AbstractDomainClanService(
            Function<String, T> creator,
            String key
    ) {
        super(creator);
        this.key = key;
    }

    @Override
    public void sendList(Player player, User user) {
        String tag = memberService.getClanTag(player, user);

        if (tag == null) {
            return;
        }

        T enemyData = getData(player, tag);

        if (enemyData == null) {
            return;
        }

        Collection<String> allies = enemyData.getAll();

        if (allies.isEmpty()) {
            messageHandler.send(player, "clan." + key + ".empty");
        } else {
            StringBuilder formattedAllies = new StringBuilder();
            int i = 0;

            for (String ally : allies) {
                formattedAllies.append(messageHandler.replacing(
                        player, "clan." + key + ".format",
                        "%tag%", ally
                ));

                if (i < allies.size() - 1) {
                    formattedAllies.append("\n");
                }
            }

            messageHandler.sendReplacing(
                    player, "clan." + key + ".message",
                    "%allies%", formattedAllies.toString()
            );
        }
    }
}
