package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import rexagon.rexaeco.RexaEco;

import java.io.File;

public class PermissionManager {
    private final RexaEco plugin;
    private YamlConfiguration config;

    public PermissionManager(RexaEco plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "permissions.yml");
        if (!file.exists()) {
            plugin.saveResource("permissions.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        
        // YAML okunduktan hemen sonra default yetkileri sunucuya enjekte eder
        injectPermissions();
    }

    private void injectPermissions() {
        ConfigurationSection permsSection = config.getConfigurationSection("permissions");
        if (permsSection == null) return;

        for (String permName : permsSection.getKeys(false)) {
            String defaultStr = permsSection.getString(permName + ".default", "op").toLowerCase();
            String description = permsSection.getString(permName + ".description", "");

            PermissionDefault permDefault;
            switch (defaultStr) {
                case "true":
                    permDefault = PermissionDefault.TRUE;
                    break;
                case "false":
                    permDefault = PermissionDefault.FALSE;
                    break;
                case "not_op":
                    permDefault = PermissionDefault.NOT_OP;
                    break;
                default:
                    permDefault = PermissionDefault.OP;
                    break;
            }

            Permission existingPerm = Bukkit.getPluginManager().getPermission(permName);
            if (existingPerm != null) {
                // Yetki zaten varsa sadece varsayılan değerini ve açıklamasını güncelle
                existingPerm.setDefault(permDefault);
                existingPerm.setDescription(description);
            } else {
                // Yetki yoksa Bukkit'e yeni yetki olarak kaydet
                Permission newPerm = new Permission(permName, description, permDefault);
                Bukkit.getPluginManager().addPermission(newPerm);
            }
        }

        // Değişikliklerin anında işlemesi için aktif oyuncuların yetkilerini yenile
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.recalculatePermissions();
        }
        
        plugin.getLogger().info("Dinamik yetkiler (permissions.yml) basariyla yuklendi ve sunucuya enjekte edildi!");
    }

    public boolean has(Player player, String path) {
        String permission = config.getString(path);
        if (permission == null || player.hasPermission(permission)) return true;
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            config.getString("no-permission-message", "&cYetkiniz yok.")));
        return false;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}