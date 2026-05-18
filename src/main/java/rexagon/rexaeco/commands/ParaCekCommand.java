package rexagon.rexaeco.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ParaCekCommand extends org.bukkit.command.Command {

    private final RexaEco plugin;

    public ParaCekCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name);
        this.setAliases(aliases);
        this.plugin = plugin;
        this.setPermission("rexaeco.paracek"); // Kilit Eklendi
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("only-players"));
            return true;
        }

        if (!player.hasPermission(this.getPermission())) {
            player.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getLanguageManager().getMessage("money-system.usage"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getLanguageManager().getMessage("money-system.invalid-amount"));
            return true;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return true;
        Economy eco = rsp.getProvider();

        boolean taxEnabled = plugin.getConfig().getBoolean("taxes.money.enabled", false);
        double taxPercent = plugin.getConfig().getDouble("taxes.money.percentage", 0.0);
        double taxAmount = taxEnabled ? (amount * (taxPercent / 100.0)) : 0.0;
        double totalCost = amount + taxAmount;

        double currentBalance = eco.getBalance(player);
        if (currentBalance < totalCost) {
            player.sendMessage(plugin.getLanguageManager().getMessage("money-system.not-enough")
                    .replace("%current%", FormatUtils.format(plugin, currentBalance)));
            if (taxEnabled && taxAmount > 0) {
                player.sendMessage(plugin.getLanguageManager().getMessage("money-system.tax-note")
                        .replace("%tax%", FormatUtils.format(plugin, taxAmount)));
            }
            return true;
        }

        eco.withdrawPlayer(player, totalCost);

        String matName = plugin.getConfig().getString("money-voucher.material", "PAPER");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.PAPER;

        ItemStack voucher = new ItemStack(mat);
        ItemMeta meta = voucher.getItemMeta();

        String formattedAmount = FormatUtils.format(plugin, amount);
        
        String itemName = plugin.getConfig().getString("money-voucher.name", "&e%amount% Dinar Çeki")
                .replace("%amount%", formattedAmount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));

        List<String> loreTemplate = plugin.getConfig().getStringList("money-voucher.lore");
        List<String> lore = new ArrayList<>();
        for (String line : loreTemplate) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line
                    .replace("%player%", player.getName())
                    .replace("%amount%", formattedAmount)));
        }
        meta.setLore(lore);

        if (plugin.getConfig().contains("money-voucher.custom-model-data")) {
            meta.setCustomModelData(plugin.getConfig().getInt("money-voucher.custom-model-data"));
        }

        NamespacedKey moneyKey = new NamespacedKey(plugin, "money_amount");
        meta.getPersistentDataContainer().set(moneyKey, PersistentDataType.DOUBLE, amount);

        voucher.setItemMeta(meta);
        player.getInventory().addItem(voucher);

        String msg = plugin.getLanguageManager().getMessage("money-system.withdrawn").replace("%amount%", formattedAmount);
        if (taxEnabled && taxAmount > 0) {
            msg += plugin.getLanguageManager().getMessage("money-system.withdrawn-tax-suffix").replace("%tax%", FormatUtils.format(plugin, taxAmount));
        }
        player.sendMessage(msg);

        return true;
    }
}