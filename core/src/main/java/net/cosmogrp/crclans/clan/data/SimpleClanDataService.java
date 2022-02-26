package net.cosmogrp.crclans.clan.data;

import net.cosmogrp.crclans.CrClansPlugin;
import net.cosmogrp.crclans.clan.data.ClanData;
import net.cosmogrp.crclans.clan.data.ClanDataService;
import net.cosmogrp.crclans.clan.service.AbstractClanService;
import net.cosmogrp.crclans.clan.service.ClanService;
import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.vault.VaultEconomyHandler;
import net.cosmogrp.storage.model.Model;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class SimpleClanDataService
        extends AbstractClanService<ClanData>
        implements ClanDataService {

    @Inject private Executor executor;

    @Inject private GlobalNotifier globalNotifier;
    @Inject private VaultEconomyHandler vaultEconomyHandler;

    private final Collection<ClanService<? extends Model>> services;

    private final FileConfiguration configuration;
    private final Pattern tagPattern;
    private final int minTagLength;
    private final int maxTagLength;

    @Inject
    public SimpleClanDataService(CrClansPlugin plugin) {
        super(ClanData::create);
        this.services = plugin.getServices();
        this.configuration = plugin.getConfig();

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

        ClanData clan = modelService.getSync(tag.toLowerCase(Locale.ROOT));

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

        CompletableFuture.runAsync(() -> {
            for (ClanService<?> service : services) {
                service.createSync(owner, tag);
            }
        }, executor)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        logHandler.reportError(
                                "Failed to save clan '%s' in creation ",
                                error, tag
                        );

                        messageHandler.send(owner, "clan.create-failed");
                        return;
                    }

                    user.setClan(tag);
                    globalNotifier.notify(
                            "clan.create-success",
                            "%tag%", tag,
                            "%creator%", owner.getName()
                    );
                });
    }
}
