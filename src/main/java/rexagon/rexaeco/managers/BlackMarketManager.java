package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rexagon.rexaeco.RexaEco;

import java.io.File;
import java.util.*;

public class BlackMarketManager {
    private final RexaEco plugin;
    private YamlConfiguration config;
    private final List<MarketItem> currentItems = new ArrayList<>();
    private long nextRefresh;

    public BlackMarketManager(RexaEco plugin) {
        this.plugin = plugin;
        loadConfig();
        startRefreshTask();
    }

    private void loadConfig() {
        File file = new File(plugin.getDataFolder(), "blackMarket.yml");
        if (!file.exists()) plugin.saveResource("blackMarket.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void startRefreshTask() {
        long interval = config.getLong("settings.refresh_interval_hours") * 3600 * 20;
        nextRefresh = System.currentTimeMillis() + (interval / 20 * 1000);
        
        Bukkit.getScheduler().runTaskTimer(plugin, this::refreshMarket, 0L, interval);
    }

    public void refreshMarket() {
        currentItems.clear();
        ConfigurationSection itemSection = config.getConfigurationSection("items");
        if (itemSection == null) return;

        List<String> keys = new ArrayList<>(itemSection.getKeys(false));
        Collections.shuffle(keys);
        
        // Örnek: Her yenilemede rastgele 5 eşya seç
        for (int i = 0; i < Math.min(5, keys.size()); i++) {
            String key = keys.get(i);
            currentItems.add(new MarketItem(itemSection.getConfigurationSection(key)));
        }
    }

    public void openMenu(Player player) {
        // Bu kısım MenuManager ile entegre edilecek veya özel GUI açılacak
        player.sendMessage("§8[§0Kara Borsa§8] §7Şu an aktif olan eşyalar:");
        for (MarketItem item : currentItems) {
            player.sendMessage("§e- " + item.displayName + " §7(Fiyat: " + item.price + ")");
        }
    }

    public static class MarketItem {
        String displayName, rarity;
        double price;
        int stock;
        public MarketItem(ConfigurationSection section) {
            this.displayName = section.getString("display_name");
            this.rarity = section.getString("rarity");
            this.price = section.getDouble("price");
            this.stock = section.getInt("stock");
        }
    }
}