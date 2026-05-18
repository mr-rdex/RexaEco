package rexagon.rexaeco.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.database.DatabaseManager;

import java.util.List;

public class RexaEconomy implements Economy {
    private final RexaEco plugin;
    private final DatabaseManager db;

    public RexaEconomy(RexaEco plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    @Override public boolean isEnabled() { return plugin.isEnabled(); }
    @Override public String getName() { return "RexaEco"; }
    @Override public boolean hasBankSupport() { return true; } // İleride banka sistemini ekleyeceğiz
    @Override public int fractionalDigits() { return 2; }
    @Override public String format(double amount) { return String.format("%.2f", amount); }
    @Override public String currencyNamePlural() { return "Coins"; }
    @Override public String currencyNameSingular() { return "Coin"; }
    
    @Override public boolean hasAccount(OfflinePlayer player) { return db.hasAccount(player.getUniqueId().toString()); }
    @Override public boolean hasAccount(String playerName) { return false; } // Sadece UUID destekliyoruz
    @Override public boolean hasAccount(OfflinePlayer player, String worldName) { return hasAccount(player); }
    @Override public boolean hasAccount(String playerName, String worldName) { return false; }

    @Override public double getBalance(OfflinePlayer player) { return db.getBalance(player.getUniqueId().toString()); }
    @Override public double getBalance(String playerName) { return 0; }
    @Override public double getBalance(OfflinePlayer player, String world) { return getBalance(player); }
    @Override public double getBalance(String playerName, String world) { return 0; }

    @Override public boolean has(OfflinePlayer player, double amount) { return getBalance(player) >= amount; }
    @Override public boolean has(String playerName, double amount) { return false; }
    @Override public boolean has(OfflinePlayer player, String worldName, double amount) { return has(player, amount); }
    @Override public boolean has(String playerName, String worldName, double amount) { return false; }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Negatif deger cekilemez");
        if (!has(player, amount)) return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Yetersiz bakiye");
        double newBalance = getBalance(player) - amount;
        db.setBalance(player.getUniqueId().toString(), newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "UUID gerekli"); }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) { return withdrawPlayer(player, amount); }
    @Override public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "UUID gerekli"); }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Negatif deger yatirilamaz");
        double newBalance = getBalance(player) + amount;
        db.setBalance(player.getUniqueId().toString(), newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override public EconomyResponse depositPlayer(String playerName, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "UUID gerekli"); }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) { return depositPlayer(player, amount); }
    @Override public EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "UUID gerekli"); }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        db.createAccount(player.getUniqueId().toString(), player.getName());
        return true;
    }

    @Override public boolean createPlayerAccount(String playerName) { return false; }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return createPlayerAccount(player); }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return false; }

    // Banka metodları ileride doldurulacak, şimdilik NotImplemented dönüyor
    @Override public EconomyResponse createBank(String name, String player) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return createBank(name, player.getName()); }
    @Override public EconomyResponse deleteBank(String name) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse bankBalance(String name) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse bankHas(String name, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return isBankOwner(name, player.getName()); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bankalar yapim asamasinda"); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return isBankMember(name, player.getName()); }
    @Override public List<String> getBanks() { return List.of(); }
}