package rexagon.rexaeco.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.XpUtil;

import java.util.ArrayList;
import java.util.List;

public class XpCekCommand extends org.bukkit.command.Command {

    private final RexaEco plugin;

    public XpCekCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name);
        this.setAliases(aliases);
        this.plugin = plugin;
        this.setPermission("rexaeco.xpcek"); // Kilit Eklendi
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
            player.sendMessage(plugin.getLanguageManager().getMessage("xp-system.usage"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getLanguageManager().getMessage("xp-system.usage"));
            return true;
        }

        if (amount != 100 && amount != 500 && amount != 1000) {
            player.sendMessage(plugin.getLanguageManager().getMessage("xp-system.usage"));
            return true;
        }

        boolean taxEnabled = plugin.getConfig().getBoolean("taxes.xp.enabled", false);
        double taxPercent = plugin.getConfig().getDouble("taxes.xp.percentage", 0.0);
        int taxAmount = taxEnabled ? (int) (amount * (taxPercent / 100.0)) : 0;
        int totalCost = amount + taxAmount;

        int currentXp = XpUtil.getTotalExperience(player);
        if (currentXp < totalCost) {
            player.sendMessage(plugin.getLanguageManager().getMessage("xp-system.not-enough")
                    .replace("%current%", String.valueOf(currentXp)));
            if (taxEnabled && taxAmount > 0) {
                player.sendMessage(plugin.getLanguageManager().getMessage("xp-system.tax-note")
                        .replace("%tax%", String.valueOf(taxAmount)));
            }
            return true;
        }

        XpUtil.setTotalExperience(player, currentXp - totalCost);

        String matName = plugin.getConfig().getString("xp-bottle.material", "EXPERIENCE_BOTTLE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.EXPERIENCE_BOTTLE;

        ItemStack xpBottle = new ItemStack(mat);
        ItemMeta meta = xpBottle.getItemMeta();

        String itemName = plugin.getConfig().getString("xp-bottle.name", "&a%amount% XP Şişesi")
                .replace("%amount%", String.valueOf(amount));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));

        List<String> loreTemplate = plugin.getConfig().getStringList("xp-bottle.lore");
        List<String> lore = new ArrayList<>();
        for (String line : loreTemplate) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line
                    .replace("%player%", player.getName())
                    .replace("%amount%", String.valueOf(amount))));
        }
        meta.setLore(lore);

        if (plugin.getConfig().contains("xp-bottle.custom-model-data")) {
            meta.setCustomModelData(plugin.getConfig().getInt("xp-bottle.custom-model-data"));
        }

        NamespacedKey xpKey = new NamespacedKey(plugin, "xp_amount");
        meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, amount);

        xpBottle.setItemMeta(meta);
        player.getInventory().addItem(xpBottle);

        String msg = plugin.getLanguageManager().getMessage("xp-system.withdrawn").replace("%amount%", String.valueOf(amount));
        if (taxEnabled && taxAmount > 0) {
            msg += plugin.getLanguageManager().getMessage("xp-system.withdrawn-tax-suffix").replace("%tax%", String.valueOf(taxAmount));
        }
        player.sendMessage(msg);

        return true;
    }
}