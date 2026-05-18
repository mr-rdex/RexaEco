package rexagon.rexaeco.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RexaEcoExpansion extends PlaceholderExpansion {

    private final RexaEco plugin;

    public RexaEcoExpansion(RexaEco plugin) {
        this.plugin = plugin;
        generatePlaceholdersFile();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rexaeco";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().isEmpty() ? "Rexagon" : plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance")) {
            if (player == null) return "0";
            double bal = plugin.getDatabaseManager().getBalance(player.getUniqueId().toString());
            return String.valueOf((long) bal);
        }

        if (params.equalsIgnoreCase("balance_formatted")) {
            if (player == null) return "0";
            double bal = plugin.getDatabaseManager().getBalance(player.getUniqueId().toString());
            return FormatUtils.format(plugin, bal);
        }

        if (params.toLowerCase().startsWith("balance_") && !params.equalsIgnoreCase("balance_formatted")) {
            String targetName = params.substring(8);
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            
            if (target.hasPlayedBefore() || target.isOnline()) {
                double targetBal = plugin.getDatabaseManager().getBalance(target.getUniqueId().toString());
                return FormatUtils.format(plugin, targetBal);
            }
            return "0";
        }

        return null;
    }

    private void generatePlaceholdersFile() {
        File file = new File(plugin.getDataFolder(), "placeholders.txt");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("=========================================");
                    writer.println("          REXA ECO PLACEHOLDERS          ");
                    writer.println("=========================================");
                    writer.println("");
                    writer.println("--- TURKCE ACIKLAMA ---");
                    writer.println("Bu eklenti asagidaki PlaceholderAPI degiskenlerini destekler.");
                    writer.println("Baska eklentilerde (Scoreboard, DeluxeMenus vs.) rahatca kullanabilirsiniz.");
                    writer.println("");
                    writer.println("1. %rexaeco_balance%");
                    writer.println("   - Oyuncunun mevcut bakiyesini düz sayi olarak dondurur.");
                    writer.println("   - Ornek Cikti: 1500000");
                    writer.println("");
                    writer.println("2. %rexaeco_balance_formatted%");
                    writer.println("   - Oyuncunun bakiyesini noktali sekilde estetik dondurur.");
                    writer.println("   - Ornek Cikti: 1.500.000");
                    writer.println("");
                    writer.println("3. %rexaeco_balance_<oyuncu_adi>%");
                    writer.println("   - Belirttiginiz oyuncunun bakiyesini noktali sekilde dondurur.");
                    writer.println("   - Kullanim Ornegi: %rexaeco_balance_byrdex_%");
                    writer.println("   - Ornek Cikti: 2.500.000");
                    writer.println("");
                    writer.println("--- ENGLISH EXPLANATION ---");
                    writer.println("This plugin supports the following PlaceholderAPI variables.");
                    writer.println("You can easily use them in other plugins (Scoreboard, DeluxeMenus, etc.).");
                    writer.println("");
                    writer.println("1. %rexaeco_balance%");
                    writer.println("   - Returns the player's current balance as a raw number.");
                    writer.println("   - Example Output: 1500000");
                    writer.println("");
                    writer.println("2. %rexaeco_balance_formatted%");
                    writer.println("   - Returns the player's balance aesthetically formatted with dots.");
                    writer.println("   - Example Output: 1.500.000");
                    writer.println("");
                    writer.println("3. %rexaeco_balance_<player_name>%");
                    writer.println("   - Returns the specified player's balance formatted with dots.");
                    writer.println("   - Usage Example: %rexaeco_balance_byrdex_%");
                    writer.println("   - Example Output: 2.500.000");
                    writer.println("");
                    writer.println("=========================================");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("placeholders.txt dosyasi olusturulamadi!");
            }
        }
    }
}