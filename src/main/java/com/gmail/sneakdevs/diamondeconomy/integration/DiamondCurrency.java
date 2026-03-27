package com.gmail.sneakdevs.diamondeconomy.integration;

import com.gmail.sneakdevs.diamondeconomy.DiamondEconomy;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.math.BigInteger;

public class DiamondCurrency implements EconomyCurrency {
    public static final DiamondCurrency INSTANCE = new DiamondCurrency();
    public static final Identifier ID = Identifier.fromNamespaceAndPath(DiamondEconomy.MODID, "diamonds");

    @Override
    public Component name() {
        return Component.literal("Diamonds");
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public String formatValue(BigInteger value, boolean precise) {
        return "$" + value;
    }

    @Override
    public BigInteger parseValue(String value) throws NumberFormatException {
        if (value.startsWith("$")) {
            value = value.substring(1);
        } else {
            return BigInteger.ZERO;
        }

        return new BigInteger(value);
    }

    @Override
    public EconomyProvider provider() {
        return DiamondEconomyProvider.INSTANCE;
    }

    @Override
    public ItemStack icon() {
        return DiamondEconomyConfig.getCurrency(0).getDefaultInstance();
    }
}
