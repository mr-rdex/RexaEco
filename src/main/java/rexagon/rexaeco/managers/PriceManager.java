package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import rexagon.rexaeco.RexaEco;
import java.io.File;
import java.lang.reflect.Method;

public class PriceManager {
    private final RexaEco plugin;
    private YamlConfiguration prices;
    private YamlConfiguration seasons;
    private String manualSeason = null;

    public PriceManager(RexaEco plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        File pFile = new File(plugin.getDataFolder(), "prices.yml");
        if (!pFile.exists()) plugin.saveResource("prices.yml", false);
        prices = YamlConfiguration.loadConfiguration(pFile);

        File sFile = new File(plugin.getDataFolder(), "seasons.yml");
        if (!sFile.exists()) plugin.saveResource("seasons.yml", false);
        seasons = YamlConfiguration.loadConfiguration(sFile);
    }

    public void setManualSeason(String season) { this.manualSeason = season; }

    public String getCurrentSeason() {
        if (manualSeason != null) return manualSeason.toUpperCase();
        
        // RealisticSeasons kontrolünü kütüphane olmadan yapıyoruz (Reflection)
        if (Bukkit.getPluginManager().isPluginEnabled("RealisticSeasons")) {
            try {
                Object api = Class.forName("me.casperge.realisticseasons.api.SeasonsAPI").getMethod("getInstance").invoke(null);
                World world = Bukkit.getWorlds().get(0);
                Method getSeasonMethod = api.getClass().getMethod("getSeason", World.class);
                Object seasonObj = getSeasonMethod.invoke(api, world);
                return seasonObj.toString().toUpperCase();
            } catch (Exception e) {
                return "SPRING";
            }
        }
        return "SPRING";
    }

    public double getPrice(String material, boolean isBuy) {
        String path = "items." + material + (isBuy ? ".buy" : ".sell");
        double basePrice = prices.getDouble(path, 0.0);
        String season = getCurrentSeason();
        double multiplier = seasons.getDouble(season + "." + material, 1.0);
        return basePrice * multiplier;
    }
}