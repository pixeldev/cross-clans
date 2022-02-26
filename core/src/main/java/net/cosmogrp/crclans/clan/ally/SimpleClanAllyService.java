package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.AbstractClanService;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Collection;

public class SimpleClanAllyService
        extends AbstractClanService<ClanAllyData>
        implements ClanAllyService {

    @Inject private ClanMemberService memberService;

    public SimpleClanAllyService() {
        super(ClanAllyData::create);
    }

    @Override
    public void sendAllies(Player sender, User user) {
        String tag = memberService.getClanTag(sender, user);

        if (tag == null) {
            return;
        }

        ClanAllyData allyData = getData(sender, tag);

        if (allyData == null) {
            return;
        }

        Collection<String> allies = allyData.getAllies();

        if (allies.isEmpty()) {
            messageHandler.send(sender, "clan.ally-list.no-allies");
        } else {
            StringBuilder formattedAllies = new StringBuilder();
            int i = 0;

            for (String ally : allies) {
                formattedAllies.append(messageHandler.replacing(
                        sender, "clan.ally-list.ally-format",
                        "%tag%", ally
                ));

                if (i < allies.size() - 1) {
                    formattedAllies.append("\n");
                }
            }

            messageHandler.sendReplacing(
                    sender, "clan.ally-list.message",
                    "%allies%", formattedAllies.toString()
            );
        }
    }
}
