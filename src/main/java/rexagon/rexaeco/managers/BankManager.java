package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.menus.RexaMenu;
import rexagon.rexaeco.utils.FormatUtils;

import java.io.File;
import java.util.List;

public class BankManager {
    private final RexaEco plugin;
    private YamlConfiguration config;

    public BankManager(RexaEco plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "bankUpgrades.yml");
        if (!file.exists()) plugin.saveResource("bankUpgrades.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void openDetailedBank(Player player) {
        RexaMenu rexaMenu = new RexaMenu();
        Inventory inv = Bukkit.createInventory(rexaMenu, 27, "§8Rexa Banka");
        String uuid = player.getUniqueId().toString();

        ItemStack info = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName("§6§lBanka Hesabınız");
        meta.setLore(List.of(
            "§7Bakiye: §e" + FormatUtils.format(plugin, plugin.getDatabaseManager().getBankBalance(uuid)),
            "§7Seviye: §6" + plugin.getDatabaseManager().getBankLevel(uuid),
            "§7Faiz Bekleme: §f" + getCooldownShort(uuid),
            "",
            "§ePara işlemleri için /banka <yatir/cek> <miktar>"
        ));
        info.setItemMeta(meta);
        inv.setItem(13, info);

        ItemStack upgrade = new ItemStack(Material.NETHER_STAR);
        ItemMeta uMeta = upgrade.getItemMeta();
        uMeta.setDisplayName("§b§lYükseltmeler");
        uMeta.setLore(List.of("§7Kapasiteyi ve faizi artırın.", "", "§eTıklayın!"));
        upgrade.setItemMeta(uMeta);
        inv.setItem(15, upgrade);
        rexaMenu.addActions(15, List.of("[console] banka upgrade_gui " + player.getName()), null);

        player.openInventory(inv);
    }

    public boolean upgradeBank(Player player) {
        String uuid = player.getUniqueId().toString();
        int nextLevel = plugin.getDatabaseManager().getBankLevel(uuid) + 1;
        if (!config.contains("levels." + nextLevel)) return false;

        double cost = config.getDouble("levels." + nextLevel + ".cost");
        double balance = plugin.getDatabaseManager().getBalance(uuid);

        if (balance >= cost) {
            plugin.getDatabaseManager().setBalance(uuid, balance - cost);
            plugin.getDatabaseManager().setBankLevel(uuid, nextLevel);
            return true;
        }
        return false;
    }

    public void claimInterest(Player player) {
        String uuid = player.getUniqueId().toString();
        long lastTime = plugin.getDatabaseManager().getLastInterestTime(uuid);
        long cooldown = config.getLong("interest_cooldown_hours", 31) * 3600000L;

        if (System.currentTimeMillis() - lastTime < cooldown) return;

        int level = plugin.getDatabaseManager().getBankLevel(uuid);
        double bankBal = plugin.getDatabaseManager().getBankBalance(uuid);
        double rate = config.getDouble("levels." + level + ".interest_rate", 0) / 100.0;
        double capacity = config.getDouble("levels." + level + ".capacity");

        double earned = Math.min(bankBal * rate, capacity - bankBal);
        if (earned > 0) {
            plugin.getDatabaseManager().setBankBalance(uuid, bankBal + earned);
            plugin.getDatabaseManager().setLastInterestTime(uuid, System.currentTimeMillis());
        }
    }

    public String getCooldownShort(String uuid) {
        long lastTime = plugin.getDatabaseManager().getLastInterestTime(uuid);
        long remaining = (lastTime + (config.getLong("interest_cooldown_hours") * 3600000L)) - System.currentTimeMillis();
        if (remaining <= 0) return "Hazır";
        return (remaining / 3600000) + "s " + ((remaining / 60000) % 60) + "d";
    }

    public String getCooldownDetailed(String uuid) {
        return getCooldownShort(uuid);
    }
}