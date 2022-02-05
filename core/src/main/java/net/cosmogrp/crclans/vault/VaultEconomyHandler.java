package net.cosmogrp.crclans.vault;

import net.cosmogrp.economy.EconomyHandler;
import net.cosmogrp.economy.account.EconomyAccount;
import net.cosmogrp.economy.transaction.amount.TransactionAmount;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultEconomyHandler implements EconomyHandler {

    private final Economy delegate;

    public VaultEconomyHandler() {
        RegisteredServiceProvider<Economy> serviceProvider =
                Bukkit.getServicesManager()
                        .getRegistration(Economy.class);

        if (serviceProvider == null) {
            throw new IllegalStateException("Vault not found!");
        }

        this.delegate = serviceProvider.getProvider();
    }

    @Override
    public @Nullable EconomyAccount getAccount(Player player) {
        return null;
    }

    @Override
    public @Nullable EconomyAccount getTargetAccount(CommandSender sender, Player target) {
        return null;
    }

    @Override
    public boolean deposit(@Nullable CommandSender source, Player target, double amount) {
        return delegate.depositPlayer(target, amount)
                .transactionSuccess();
    }

    @Override
    public boolean withdraw(@Nullable CommandSender source, Player target, double amount) {
        return delegate.withdrawPlayer(target, amount)
                .transactionSuccess();
    }

    @Override
    public boolean transfer(Player source, Player target, TransactionAmount amount) {
        return false;
    }

    @Override
    public boolean hasEnough(Player source, double amount) {
        return delegate.has(source, amount);
    }

    @Override
    public double getBalance(Player source) {
        return delegate.getBalance(source);
    }

    @Override
    public void sendBalance(Player source) {

    }
}
