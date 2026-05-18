package rexagon.rexaeco.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KillCoinManager {
    private final RexaEco plugin;
    private YamlConfiguration config;

    public KillCoinManager(RexaEco plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    // BURASI PUBLIC OLDU (Reload hatasını çözen ana değişiklik)
    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "killCoins.yml");
        if (!file.exists()) plugin.saveResource("killCoins.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void handleKill(Player player, String entityType) {
        if (config == null || !config.getBoolean("enabled", true)) return;
        if (!config.contains("entities." + entityType)) return;

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String uuid = player.getUniqueId().toString();

        double min = config.getDouble("entities." + entityType + ".min_coins", 1.0);
        double max = config.getDouble("entities." + entityType + ".max_coins", 10.0);
        double reward = min + (Math.random() * (max - min));

        double balance = plugin.getDatabaseManager().getBalance(uuid);
        plugin.getDatabaseManager().setBalance(uuid, balance + reward);
        
        plugin.getDatabaseManager().addDailyCoins(uuid, dateStr, reward);

        // Mesaj gönderimi
        String msg = plugin.getLanguageManager().getMessage("killcoins.earned")
                .replace("%amount%", FormatUtils.format(plugin, reward))
                .replace("%entity%", entityType);
        player.sendMessage(msg);
    }
}