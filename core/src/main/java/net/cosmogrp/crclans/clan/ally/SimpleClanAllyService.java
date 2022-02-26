package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.AbstractClanService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.member.ClanMemberService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Collection;

public class SimpleClanAllyService
        extends AbstractClanService<ClanAllyData>
        implements ClanAllyService {

    @Inject private ClanMemberService memberService;
    @Inject private GlobalNotifier globalNotifier;

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

    @Override
    public void removeAlly(
            Player sender, User user,
            ClanAllyData targetAllyData
    ) {
        memberService.computeAsOwner(
                sender, user,
                memberData -> {
                    String senderTag = memberData.getId();
                    if (!targetAllyData.isAlly(senderTag)) {
                        messageHandler.send(sender, "clan.not-ally");
                        return;
                    }

                    ClanAllyData senderAllyData = getData(sender, senderTag);

                    if (senderAllyData == null) {
                        return;
                    }

                    String targetTag = targetAllyData.getId();

                    ClanMemberData targetMemberData = memberService
                            .getData(sender, targetTag);

                    if (targetMemberData == null) {
                        return;
                    }

                    globalNotifier.notify(
                            targetMemberData.getOnlineIdMembers(),
                            "clan.ally-remove-target-members",
                            "%tag%", senderTag
                    );

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.ally-remove-members",
                            "%tag%", targetTag
                    );

                    senderAllyData.removeAlly(targetTag);
                    targetAllyData.removeAlly(senderTag);

                    save(sender, senderAllyData);
                    save(sender, targetAllyData);
                }
        );
    }

    @Override
    public void removeAlly(String source, String target) {
        ClanAllyData senderAllyData = getData(source);

        if (senderAllyData == null) {
            return;
        }

        ClanMemberData targetMemberData = memberService
                .getData(target);

        if (targetMemberData == null) {
            return;
        }

        globalNotifier.notify(
                targetMemberData.getOnlineIdMembers(),
                "clan.ally-remove-target-members",
                "%tag%", source
        );


        senderAllyData.removeAlly(target);
        save(senderAllyData);
    }
}
