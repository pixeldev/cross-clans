package net.cosmogrp.crclans.clan;

import me.yushust.message.MessageHandler;
import net.cosmogrp.crclans.log.LogHandler;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.user.clan.ClanUserService;
import net.cosmogrp.crclans.vault.VaultEconomyHandler;
import net.cosmogrp.storage.AsyncModelService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class SimpleClanService implements ClanService {

    @Inject private AsyncModelService<Clan> modelService;
    @Inject private MessageHandler messageHandler;
    @Inject private GlobalNotifier globalNotifier;
    @Inject private VaultEconomyHandler vaultEconomyHandler;
    @Inject private ClanUserService clanUserService;
    @Inject private LogHandler logHandler;

    private final FileConfiguration configuration;
    private final Pattern tagPattern;
    private final int minTagLength;
    private final int maxTagLength;

    @Inject
    public SimpleClanService(FileConfiguration configuration) {
        this.configuration = configuration;

        String tagPattern = configuration.getString("clan.tag-pattern");

        if (tagPattern == null) {
            tagPattern = "^[a-zA-Z0-9]{3,6}$";
            minTagLength = 3;
            maxTagLength = 6;
        } else {
            minTagLength = configuration.getInt("clan.tag-min-length", 3);
            maxTagLength = configuration.getInt("clan.tag-max-length", 6);
            tagPattern = String.format(tagPattern, minTagLength, maxTagLength);
        }

        this.tagPattern = Pattern.compile(tagPattern);
    }

    @Override
    public @Nullable Clan getClan(String tag) {
        return modelService.getSync(tag);
    }

    @Override
    public void createClan(User user, Player owner, String tag) {
        if (user.hasClan()) {
            messageHandler.send(owner, "clan.already-in-clan");
            return;
        }

        if (!tagPattern.matcher(tag).matches()) {
            messageHandler.sendReplacing(
                    owner, "clan.invalid-tag",
                    "%min%", minTagLength,
                    "%max%", maxTagLength
            );
            return;
        }

        Clan clan = modelService.getSync(tag);

        if (clan != null) {
            messageHandler.send(owner, "clan.already-exists");
            return;
        }

        if (!vaultEconomyHandler.withdraw(
                owner,
                configuration.getDouble("clans.create-cost")
        )) {
            messageHandler.send(owner, "clan.not-enough-money");
            return;
        }

        clan = Clan.create(owner, tag);
        user.setClan(clan);

        modelService.save(clan)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        messageHandler.send(owner, "clan.create-failed");
                        return;
                    }

                    globalNotifier.notify(
                            "clan.create-success",
                            "%tag%", tag,
                            "%creator%", owner.getName()
                    );
                });
    }

    @Override
    public void deleteClan(User user, Player owner) {
        clanUserService.executeAsOwner(
                owner, user,
                clan -> modelService
                        .delete(clan)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                logHandler.reportError(
                                        "Failed to delete clan '%s'", error,
                                        clan.getId()
                                );

                                messageHandler.send(owner, "clan.delete-failed");
                                return;
                            }

                            // just remove clan from the owner
                            // we will wait to members get connected to the server
                            user.setClan(null);

                            // TODO: remove clan from all online members
                            messageHandler.send(owner, "clan.delete-success");
                        }));
    }
}
