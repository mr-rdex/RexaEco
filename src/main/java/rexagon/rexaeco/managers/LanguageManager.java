package rexagon.rexaeco.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import rexagon.rexaeco.RexaEco;
import java.io.File;

public class LanguageManager {
    private final RexaEco plugin;
    private YamlConfiguration langConfig;

    public LanguageManager(RexaEco plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        String lang = plugin.getConfig().getString("language", "tr");
        String fileName = "messages_" + lang + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) plugin.saveResource(fileName, false);
        
        // Burası reload için kritik: Dosyayı baştan okur
        langConfig = YamlConfiguration.loadConfiguration(file);
    }

    public String getMessage(String path) {
        String message = langConfig.getString(path);
        if (message == null) return "§cEksik Mesaj: " + path;
        String prefix = langConfig.getString("prefix", "");
        return ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", prefix));
    }
}