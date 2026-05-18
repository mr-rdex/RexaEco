package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import rexagon.rexaeco.RexaEco;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeaderboardManager {
    private final RexaEco plugin;
    private List<Entry> topBank = new ArrayList<>();
    private List<Entry> topKillCoins = new ArrayList<>();

    public LeaderboardManager(RexaEco plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateLeaderboards, 0L, 6000L); // 5 dakikada bir günceller
    }

    public void updateLeaderboards() {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        topBank = plugin.getDatabaseManager().getTopBank(100);
        topKillCoins = plugin.getDatabaseManager().getTopKillCoins(dateStr, 100);
    }

    public String getBankTopName(int pos) {
        if (pos <= 0 || pos > topBank.size()) return "Bilinmiyor";
        return topBank.get(pos - 1).name;
    }

    public double getBankTopValue(int pos) {
        if (pos <= 0 || pos > topBank.size()) return 0.0;
        return topBank.get(pos - 1).value;
    }

    public int getBankRank(String uuid) {
        for (int i = 0; i < topBank.size(); i++) {
            if (topBank.get(i).uuid.equals(uuid)) return i + 1;
        }
        return plugin.getDatabaseManager().getBankRankFromDB(uuid);
    }

    public String getKillCoinTopName(int pos) {
        if (pos <= 0 || pos > topKillCoins.size()) return "Bilinmiyor";
        return topKillCoins.get(pos - 1).name;
    }

    public double getKillCoinTopValue(int pos) {
        if (pos <= 0 || pos > topKillCoins.size()) return 0.0;
        return topKillCoins.get(pos - 1).value;
    }

    public int getKillCoinRank(String uuid) {
        for (int i = 0; i < topKillCoins.size(); i++) {
            if (topKillCoins.get(i).uuid.equals(uuid)) return i + 1;
        }
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return plugin.getDatabaseManager().getKillCoinRankFromDB(uuid, dateStr);
    }

    public static class Entry {
        public String uuid, name;
        public double value;
        public Entry(String uuid, String name, double value) {
            this.uuid = uuid; this.name = name; this.value = value;
        }
    }
}