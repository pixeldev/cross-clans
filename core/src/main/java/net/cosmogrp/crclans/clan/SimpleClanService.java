package net.cosmogrp.crclans.clan;

import me.yushust.message.MessageHandler;
import net.cosmogrp.economy.EconomyHandler;
import net.cosmogrp.storage.AsyncModelService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.regex.Pattern;

public class SimpleClanService implements ClanService {

    @Inject private AsyncModelService<Clan> modelService;
    @Inject private MessageHandler messageHandler;

    @Inject
    @Named("vault")
    private EconomyHandler vaultEconomy;

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
    public void createClan(Player owner, String tag) {
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

        if (vaultEconomy.withdraw(
                owner,
                configuration.getDouble("clan.create-cost")
        )) {
            clan = new Clan(tag, owner);
            modelService.save(clan)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            messageHandler.send(owner, "clan.create-failed");
                            return;
                        }

                        messageHandler.sendReplacing(
                                owner,
                                "clan.create-success",
                                "%tag%", tag
                        );
                    });
        }
    }
}
