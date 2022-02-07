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

    private Economy delegate;

    public void setup() {
        RegisteredServiceProvider<Economy> serviceProvider =
                Bukkit.getServicesManager()
                        .getRegistration(Economy.class);

        if (serviceProvider == null) {
            this.delegate = null;
        } else {
            this.delegate = serviceProvider.getProvider();
        }
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
        if (delegate == null) {
            return true;
        }

        return delegate.depositPlayer(target, amount)
                .transactionSuccess();
    }

    @Override
    public boolean withdraw(@Nullable CommandSender source, Player target, double amount) {
        if (delegate == null) {
            return true;
        }

        return delegate.withdrawPlayer(target, amount)
                .transactionSuccess();
    }

    @Override
    public boolean transfer(Player source, Player target, TransactionAmount amount) {
        return false;
    }

    @Override
    public boolean hasEnough(Player source, double amount) {
        if (delegate == null) {
            return true;
        }

        return delegate.has(source, amount);
    }

    @Override
    public double getBalance(Player source) {
        if (delegate == null) {
            return 0;
        }

        return delegate.getBalance(source);
    }

    @Override
    public void sendBalance(Player source) {

    }
}
