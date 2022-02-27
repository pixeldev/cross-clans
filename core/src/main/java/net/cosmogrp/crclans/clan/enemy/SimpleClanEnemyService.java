package net.cosmogrp.crclans.clan.enemy;

import net.cosmogrp.crclans.clan.ally.ClanAllyData;
import net.cosmogrp.crclans.clan.ally.ClanAllyService;
import net.cosmogrp.crclans.clan.member.ClanMemberData;
import net.cosmogrp.crclans.clan.service.AbstractDomainClanService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleClanEnemyService
        extends AbstractDomainClanService<ClanEnemyData>
        implements ClanEnemyService {

    @Inject private GlobalNotifier globalNotifier;
    @Inject private ClanAllyService allyService;

    public SimpleClanEnemyService() {
        super(ClanEnemyData::create, "enemy-list");
    }

    @Override
    public void addEnemy(
            Player sender, User user,
            ClanMemberData targetMemberData
    ) {
        memberService.computeAsOwner(
                sender, user,
                memberData -> {
                    String tag = memberData.getId();

                    ClanAllyData allyData = allyService
                            .getData(sender, tag);

                    if (allyData == null) {
                        return;
                    }

                    String targetTag = targetMemberData.getId();

                    if (allyData.contains(targetTag)) {
                        messageHandler.send(sender, "clan.ally-enemy");
                        return;
                    }

                    ClanEnemyData enemyData = getData(sender, tag);

                    if (enemyData == null) {
                        return;
                    }

                    if (enemyData.contains(targetTag)) {
                        messageHandler.send(sender, "clan.already-enemy");
                        return;
                    }

                    enemyData.add(targetTag);

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.enemy-added-members",
                            "%tag%", targetTag
                    );

                    globalNotifier.notify(
                            targetMemberData.getOnlineIdMembers(),
                            "clan.enemy-added-target-members",
                            "%tag%", tag
                    );

                    save(sender, enemyData);
                }
        );
    }

    @Override
    public void removeEnemy(
            Player sender, User user,
            String targetClan
    ) {
        memberService.computeAsOwner(
                sender, user,
                memberData -> {
                    String tag = memberData.getId();
                    ClanEnemyData enemyData = getData(sender, tag);

                    if (enemyData == null) {
                        return;
                    }

                    if (!enemyData.contains(targetClan)) {
                        messageHandler.send(sender, "clan.not-enemy");
                        return;
                    }

                    enemyData.remove(targetClan);

                    globalNotifier.notify(
                            memberData.getOnlineIdMembers(),
                            "clan.enemy-removed-members",
                            "%tag%", targetClan
                    );

                    ClanMemberData targetMemberData = memberService
                            .getData(targetClan);

                    if (targetMemberData != null) {
                        globalNotifier.notify(
                                targetMemberData.getOnlineIdMembers(),
                                "clan.enemy-removed-target-members",
                                "%tag%", tag
                        );
                    }

                    save(sender, enemyData);
                }
        );
    }
}
