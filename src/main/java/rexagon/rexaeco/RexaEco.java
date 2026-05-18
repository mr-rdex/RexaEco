package rexagon.rexaeco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import rexagon.rexaeco.commands.*;
import rexagon.rexaeco.database.DatabaseManager;
import rexagon.rexaeco.economy.RexaEconomy;
import rexagon.rexaeco.hooks.RexaEcoExpansion;
import rexagon.rexaeco.listeners.*;
import rexagon.rexaeco.managers.*;
import rexagon.rexaeco.menus.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class RexaEco extends JavaPlugin {
    
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private PermissionManager permissionManager;
    private PriceManager priceManager;
    private BankManager bankManager;
    private KillCoinManager killCoinManager;
    private LeaderboardManager leaderboardManager;
    private GambleManager gambleManager;
    private BlackMarketManager blackMarketManager;
    private MarketManager marketManager;
    private MenuManager menuManager;
    private SellMenuListener sellMenuListener;

    @Override
    public void onEnable() {
        // 1. Klasör ve Config Yapılandırması
        saveDefaultConfig();
        createMenuFolder();

        // 2. Veritabanı Bağlantısı
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();
        
        // 3. Manager Başlatmaları
        languageManager = new LanguageManager(this);
        permissionManager = new PermissionManager(this);
        priceManager = new PriceManager(this);
        bankManager = new BankManager(this);
        killCoinManager = new KillCoinManager(this);
        leaderboardManager = new LeaderboardManager(this);
        gambleManager = new GambleManager(this);
        blackMarketManager = new BlackMarketManager(this);
        marketManager = new MarketManager(this);
        menuManager = new MenuManager(this);
        
        // 4. Listener Kayıtları
        this.sellMenuListener = new SellMenuListener(this);
        getServer().getPluginManager().registerEvents(new MoneyVoucherListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new KillListener(this), this);
        getServer().getPluginManager().registerEvents(new MarketListener(this), this); 
        getServer().getPluginManager().registerEvents(sellMenuListener, this); 
        getServer().getPluginManager().registerEvents(new XpBottleListener(this), this);
        getServer().getPluginManager().registerEvents(new SellWandListener(this), this); // SellWand Listener

        // 5. Ekonomi ve Komut Sistemleri
        setupEconomy();
        registerCommands();

        // 6. Hooklar (PAPI)
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RexaEcoExpansion(this).register();
        }
        
        getLogger().info("===============================");
        getLogger().info("RexaEco v1.0 aktif edildi!");
        getLogger().info("Yapimci: Rexagon");
        getLogger().info("===============================");
        
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> leaderboardManager.updateLeaderboards());
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
        getLogger().info("RexaEco pasif hale getirildi.");
    }

    public void reloadPlugin() {
        reloadConfig(); 
        
        if (languageManager != null) languageManager.loadLanguage();
        if (permissionManager != null) permissionManager.loadConfig();
        if (priceManager != null) priceManager.loadConfigs();
        if (bankManager != null) bankManager.loadConfig();
        if (killCoinManager != null) killCoinManager.loadConfig();
        if (marketManager != null) marketManager.loadConfigs();
        if (sellMenuListener != null) sellMenuListener.loadConfig();
        
        getLogger().info("RexaEco: Tum .yml dosyalari ve menuler basariyla yenilendi!");
    }

    private void createMenuFolder() {
        File menuFolder = new File(getDataFolder(), "menus");
        if (!menuFolder.exists()) {
            menuFolder.mkdirs();
        }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault bulunamadi! Ekonomi ozellikleri calismayacak.");
            return;
        }
        getServer().getServicesManager().register(Economy.class, new RexaEconomy(this), this, ServicePriority.Highest);
    }

    private void registerCommands() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            // Komutlar config.yml'deki isimlere göre kaydedilir
            registerConfigCommand(commandMap, "money", (n, a) -> new MoneyCommand(n, a, this));
            registerConfigCommand(commandMap, "admin", (n, a) -> new EcoCommand(n, a, this));
            registerConfigCommand(commandMap, "bank", (n, a) -> new BankCommand(n, a, this));
            registerConfigCommand(commandMap, "gamble", (n, a) -> new GambleCommand(n, a, this));
            registerConfigCommand(commandMap, "blackmarket", (n, a) -> new BlackMarketCommand(n, a, this));
            registerConfigCommand(commandMap, "market", (n, a) -> new MarketCommand(n, a, this));
            registerConfigCommand(commandMap, "paracek", (n, a) -> new ParaCekCommand(n, a, this));
            registerConfigCommand(commandMap, "sell", (n, a) -> new SellCommand(n, a, this));
            registerConfigCommand(commandMap, "pay", (n, a) -> new PayCommand(n, a, this));
            registerConfigCommand(commandMap, "xpcek", (n, a) -> new XpCekCommand(n, a, this));
            
            // YENİ: SellWand komutu kendi sisteminle entegre edildi!
            registerConfigCommand(commandMap, "sellwand", (n, a) -> new SellWandCommand(n, a, this));
            
            // Sabit Komutlar
            commandMap.register(getName().toLowerCase(), new SeasonCommand(this));

        } catch (Exception e) {
            getLogger().severe("Komutlar kaydedilirken hata olustu: " + e.getMessage());
        }
    }

    private void registerConfigCommand(CommandMap map, String key, CommandCreator creator) {
        String name = getConfig().getString("commands." + key + ".name", key);
        List<String> aliases = getConfig().getStringList("commands." + key + ".aliases");
        map.register(getName().toLowerCase(), creator.create(name, aliases));
    }

    private interface CommandCreator {
        org.bukkit.command.Command create(String name, List<String> aliases);
    }

    // --- GETTERS ---
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public PermissionManager getPermissionManager() { return permissionManager; }
    public PriceManager getPriceManager() { return priceManager; }
    public BankManager getBankManager() { return bankManager; }
    public KillCoinManager getKillCoinManager() { return killCoinManager; }
    public LeaderboardManager getLeaderboardManager() { return leaderboardManager; }
    public GambleManager getGambleManager() { return gambleManager; }
    public BlackMarketManager getBlackMarketManager() { return blackMarketManager; }
    public MarketManager getMarketManager() { return marketManager; }
    public MenuManager getMenuManager() { return menuManager; }
    public SellMenuListener getSellMenuListener() { return sellMenuListener; }
}