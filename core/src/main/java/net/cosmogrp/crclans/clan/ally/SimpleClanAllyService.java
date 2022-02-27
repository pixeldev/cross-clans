package net.cosmogrp.crclans.clan.ally;

import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.service.AbstractDomainClanService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanAllyService
        extends AbstractDomainClanService<ClanAllyData>
        implements ClanAllyService {

    @Inject private GlobalNotifier globalNotifier;

    public SimpleClanAllyService() {
        super(ClanAllyData::create, "ally-list");
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
                    if (!targetAllyData.contains(senderTag)) {
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

                    senderAllyData.remove(targetTag);
                    targetAllyData.remove(senderTag);

                    save(sender, senderAllyData);
                    save(sender, targetAllyData);
                }
        );
    }

    @Override
    public void removeAlly(String source, String target) {
        ClanAllyData targetAllyData = getData(target);

        if (targetAllyData == null) {
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

        targetAllyData.remove(source);
        save(targetAllyData);
    }
}
