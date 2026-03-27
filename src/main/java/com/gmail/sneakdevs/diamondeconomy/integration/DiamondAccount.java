package com.gmail.sneakdevs.diamondeconomy.integration;

import com.gmail.sneakdevs.diamondeconomy.DiamondEconomy;
import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import eu.pb4.common.economy.api.EconomyTransaction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.math.BigInteger;
import java.util.UUID;

public class DiamondAccount implements EconomyAccount {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(DiamondEconomy.MODID, "deaccount");
    private final UUID uuid;
    private final String uuidString;

    public DiamondAccount(UUID uuid) {
        this.uuid = uuid;
        this.uuidString = uuid.toString();
    }

    @Override
    public Component name() {
        return Component.literal("DEAccount");
    }

    @Override
    public UUID owner() {
        return uuid;
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public BigInteger balance() {
        return BigInteger.valueOf(DiamondUtils.getDatabaseManager().getBalanceFromUUID(uuidString));
    }

    @Override
    public EconomyTransaction canIncreaseBalance(BigInteger value) {
        BigInteger currentBal = BigInteger.valueOf(DiamondUtils.getDatabaseManager().getBalanceFromUUID(uuidString));
        BigInteger newBal = currentBal.add(value);
        if (newBal.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
            return new EconomyTransaction.Simple(
                    false,
                    Component.literal("Integer overflow ($" + newBal + " > $" + Integer.MAX_VALUE + ")"),
                    currentBal,
                    currentBal,
                    BigInteger.ZERO,
                    this
            );
        }

        return new EconomyTransaction.Simple(
                true,
                Component.literal("Added $" + value + " to the account"),
                newBal,
                currentBal,
                value,
                this
        );
    }

    @Override
    public EconomyTransaction canDecreaseBalance(BigInteger value) {
        BigInteger currentBal = BigInteger.valueOf(DiamondUtils.getDatabaseManager().getBalanceFromUUID(uuidString));
        BigInteger newBal = currentBal.subtract(value);
        if (newBal.compareTo(BigInteger.ZERO) < 0) {
            return new EconomyTransaction.Simple(
                    false,
                    Component.literal("Not enough money to take $" + value + "from your account of $" + currentBal),
                    currentBal,
                    currentBal,
                    BigInteger.ZERO,
                    this
            );
        }

        return new EconomyTransaction.Simple(
                true,
                Component.literal("Added $" + value + " to your account"),
                newBal,
                currentBal,
                value,
                this
        );
    }

    @Override
    public void setBalance(BigInteger value) {
        DiamondUtils.getDatabaseManager().setBalance(uuidString, value.intValue());
    }

    @Override
    public EconomyProvider provider() {
        return DiamondEconomyProvider.INSTANCE;
    }

    @Override
    public EconomyCurrency currency() {
        return DiamondCurrency.INSTANCE;
    }

    @Override
    public ItemStack accountIcon() {
        return DiamondEconomyConfig.getCurrency(0).getDefaultInstance();
    }
}
