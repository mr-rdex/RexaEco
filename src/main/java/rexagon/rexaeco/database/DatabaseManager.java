package rexagon.rexaeco.database;

import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.managers.LeaderboardManager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final RexaEco plugin;
    private Connection connection;

    public DatabaseManager(RexaEco plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File dbFile = new File(dataFolder, "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createTables();
            plugin.getLogger().info("SQLite baglantisi basarili.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Veritabani hatasi: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(16), balance DOUBLE DEFAULT 0.0, bank_balance DOUBLE DEFAULT 0.0, bank_level INTEGER DEFAULT 1, last_interest BIGINT DEFAULT 0);");
            stmt.execute("CREATE TABLE IF NOT EXISTS kill_daily_coins (uuid VARCHAR(36), date_str VARCHAR(10), total_coins DOUBLE DEFAULT 0.0, PRIMARY KEY(uuid, date_str));");
            stmt.execute("CREATE TABLE IF NOT EXISTS kill_entity_stats (uuid VARCHAR(36), date_str VARCHAR(10), entity VARCHAR(32), kills INTEGER DEFAULT 0, PRIMARY KEY(uuid, date_str, entity));");
        }
    }

    public boolean hasAccount(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid); return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public void createAccount(String uuid, String name) {
        if (hasAccount(uuid)) return;
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO players (uuid, name) VALUES (?, ?)")) {
            ps.setString(1, uuid); ps.setString(2, name); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public double getBalance(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT balance FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException ignored) {} return 0.0;
    }

    public void setBalance(String uuid, double amount) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET balance = ? WHERE uuid = ?")) {
            ps.setDouble(1, amount); ps.setString(2, uuid); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public double getBankBalance(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT bank_balance FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("bank_balance");
        } catch (SQLException ignored) {} return 0.0;
    }

    public void setBankBalance(String uuid, double amount) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET bank_balance = ? WHERE uuid = ?")) {
            ps.setDouble(1, amount); ps.setString(2, uuid); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public int getBankLevel(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT bank_level FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("bank_level");
        } catch (SQLException ignored) {} return 1;
    }

    public void setBankLevel(String uuid, int level) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET bank_level = ? WHERE uuid = ?")) {
            ps.setInt(1, level); ps.setString(2, uuid); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public long getLastInterestTime(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT last_interest FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("last_interest");
        } catch (SQLException ignored) {} return 0;
    }

    public void setLastInterestTime(String uuid, long time) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET last_interest = ? WHERE uuid = ?")) {
            ps.setLong(1, time); ps.setString(2, uuid); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public double getDailyCoins(String uuid, String dateStr) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT total_coins FROM kill_daily_coins WHERE uuid = ? AND date_str = ?")) {
            ps.setString(1, uuid); ps.setString(2, dateStr);
            ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getDouble("total_coins");
        } catch (SQLException ignored) {} return 0.0;
    }

    public void addDailyCoins(String uuid, String dateStr, double amount) {
        double current = getDailyCoins(uuid, dateStr);
        try (PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO kill_daily_coins (uuid, date_str, total_coins) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid); ps.setString(2, dateStr); ps.setDouble(3, current + amount); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public int getDailyKills(String uuid, String dateStr, String entity) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT kills FROM kill_entity_stats WHERE uuid = ? AND date_str = ? AND entity = ?")) {
            ps.setString(1, uuid); ps.setString(2, dateStr); ps.setString(3, entity);
            ResultSet rs = ps.executeQuery(); if (rs.next()) return rs.getInt("kills");
        } catch (SQLException ignored) {} return 0;
    }

    public void addDailyKills(String uuid, String dateStr, String entity, int amount) {
        int current = getDailyKills(uuid, dateStr, entity);
        try (PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO kill_entity_stats (uuid, date_str, entity, kills) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, uuid); ps.setString(2, dateStr); ps.setString(3, entity); ps.setInt(4, current + amount); ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    // Leaderboard Metodları
    public List<LeaderboardManager.Entry> getTopBank(int limit) {
        List<LeaderboardManager.Entry> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid, name, bank_balance FROM players ORDER BY bank_balance DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new LeaderboardManager.Entry(rs.getString("uuid"), rs.getString("name"), rs.getDouble("bank_balance")));
        } catch (SQLException ignored) {}
        return list;
    }

    public List<LeaderboardManager.Entry> getTopKillCoins(String dateStr, int limit) {
        List<LeaderboardManager.Entry> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT k.uuid, p.name, k.total_coins FROM kill_daily_coins k LEFT JOIN players p ON k.uuid = p.uuid WHERE k.date_str = ? ORDER BY k.total_coins DESC LIMIT ?")) {
            ps.setString(1, dateStr); ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                list.add(new LeaderboardManager.Entry(rs.getString("uuid"), name == null ? "Bilinmiyor" : name, rs.getDouble("total_coins")));
            }
        } catch (SQLException ignored) {}
        return list;
    }

    public int getBankRankFromDB(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) + 1 as rank FROM players WHERE bank_balance > (SELECT bank_balance FROM players WHERE uuid = ?)")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("rank");
        } catch (SQLException ignored) {} return -1;
    }

    public int getKillCoinRankFromDB(String uuid, String dateStr) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) + 1 as rank FROM kill_daily_coins WHERE date_str = ? AND total_coins > (SELECT total_coins FROM kill_daily_coins WHERE uuid = ? AND date_str = ?)")) {
            ps.setString(1, dateStr); ps.setString(2, uuid); ps.setString(3, dateStr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("rank");
        } catch (SQLException ignored) {} return -1;
    }
}