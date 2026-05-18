package rexagon.rexaeco.menus;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rexagon.rexaeco.RexaEco;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {
    private final RexaEco plugin;
    private final Map<String, File> menuFiles = new HashMap<>();

    public MenuManager(RexaEco plugin) {
        this.plugin = plugin;
        loadMenus();
    }

    public void loadMenus() {
        menuFiles.clear();
        File menuFolder = new File(plugin.getDataFolder(), "menus");
        if (!menuFolder.exists()) {
            menuFolder.mkdirs();
            createDefaultBankMenu(menuFolder);
        }

        File[] files = menuFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                menuFiles.put(file.getName().replace(".yml", ""), file);
            }
        }
    }

    public void openMenu(Player player, String menuName) {
        if (!menuFiles.containsKey(menuName)) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(menuFiles.get(menuName));
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("menu_title", "Menü"));
        int size = config.getInt("size", 27);

        RexaMenu rexaMenu = new RexaMenu();
        Inventory inv = Bukkit.createInventory(rexaMenu, size, PlaceholderAPI.setPlaceholders(player, title));
        rexaMenu.setInventory(inv);

        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection itemCfg = items.getConfigurationSection(key);
                if (itemCfg == null) continue;

                Material material = Material.matchMaterial(itemCfg.getString("material", "STONE"));
                if (material == null) material = Material.STONE;

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    if (itemCfg.contains("display_name")) {
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, itemCfg.getString("display_name"))));
                    }
                    if (itemCfg.contains("lore")) {
                        List<String> lore = new ArrayList<>();
                        for (String line : itemCfg.getStringList("lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, line)));
                        }
                        meta.setLore(lore);
                    }
                    if (itemCfg.contains("custom_model_data")) {
                        meta.setCustomModelData(itemCfg.getInt("custom_model_data"));
                    }
                    item.setItemMeta(meta);
                }

                int slot = itemCfg.getInt("slot", 0);
                inv.setItem(slot, item);

                rexaMenu.addActions(slot, itemCfg.getStringList("left_click_commands"), itemCfg.getStringList("right_click_commands"));
            }
        }
        player.openInventory(inv);
    }

    private void createDefaultBankMenu(File folder) {
        File defaultMenu = new File(folder, "bank.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("menu_title", "&8Banka İşlemleri");
        config.set("size", 27);
        
        config.set("items.bilgi.material", "GOLD_INGOT");
        config.set("items.bilgi.slot", 11);
        config.set("items.bilgi.custom_model_data", 100);
        config.set("items.bilgi.display_name", "&eBanka Bakiyesi");
        config.set("items.bilgi.lore", List.of("&7Mevcut: &a%rexaeco_bank_balance% Coin", "&7Banka Seviyesi: &6%rexaeco_bank_level%"));
        
        config.set("items.yukselt.material", "NETHER_STAR");
        config.set("items.yukselt.slot", 13);
        config.set("items.yukselt.display_name", "&aBankayı Yükselt");
        config.set("items.yukselt.lore", List.of("&7Tıklayarak bankanızı bir üst seviyeye yükseltin."));
        config.set("items.yukselt.left_click_commands", List.of("[close]", "[console] rexaeco upgradebank %player%"));

        config.set("items.faiz.material", "EMERALD");
        config.set("items.faiz.slot", 15);
        config.set("items.faiz.display_name", "&aFaiz Al");
        config.set("items.faiz.lore", List.of("&7Bekleme Süresi: &e%rexaeco_interest_cooldown_detailed%", "", "&eFaiz almak için tıklayın!"));
        config.set("items.faiz.left_click_commands", List.of("[close]", "[console] rexaeco claiminterest %player%"));
        
        try { config.save(defaultMenu); } catch (Exception ignored) {}
    }
}