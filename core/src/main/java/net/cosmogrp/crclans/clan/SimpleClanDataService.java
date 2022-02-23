package net.cosmogrp.crclans.clan;

import net.cosmogrp.crclans.notifier.global.GlobalNotifier;
import net.cosmogrp.crclans.user.User;
import net.cosmogrp.crclans.vault.VaultEconomyHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class SimpleClanDataService
        extends AbstractClanService<ClanData>
        implements ClanDataService {

    @Inject private GlobalNotifier globalNotifier;
    @Inject private VaultEconomyHandler vaultEconomyHandler;

    private final FileConfiguration configuration;
    private final Pattern tagPattern;
    private final int minTagLength;
    private final int maxTagLength;

    @Inject
    public SimpleClanDataService(FileConfiguration configuration) {
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

        clan = ClanData.create(tag, owner);
        user.setClan(tag);

        modelService.save(clan)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        logHandler.reportError(
                                "Failed to save clan '%s' in creation ",
                                error, tag
                        );

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
    public void computeAsOwner(
            Player player, User user,
            Consumer<ClanData> action
    ) {
        String tag = user.getClanTag();

        if (tag == null) {
            messageHandler.send(player, "clan.not-in-clan");
            return;
        }

        ClanData clan = getData(player, tag);

        if (clan == null) {
            return;
        }

        if (!clan.isOwner(player)) {
            messageHandler.send(player, "clan.not-owner");
            return;
        }

        action.accept(clan);
    }
}
