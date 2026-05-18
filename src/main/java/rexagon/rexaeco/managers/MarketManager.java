package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.menus.RexaMenu;
import rexagon.rexaeco.utils.FormatUtils;

import java.io.File;
import java.util.*;

public class MarketManager {
    private final RexaEco plugin;
    private YamlConfiguration config, buyConfig, sellConfig, stackBuyConfig, stackSellConfig;
    private final Map<UUID, Integer> selectedAmount = new HashMap<>();

    public MarketManager(RexaEco plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        File folder = new File(plugin.getDataFolder(), "menus");
        if (!folder.exists()) folder.mkdirs();
        config = loadAndSave(new File(folder, "market.yml"), "menus/market.yml");
        buyConfig = loadAndSave(new File(folder, "buy_gui.yml"), "menus/buy_gui.yml");
        sellConfig = loadAndSave(new File(folder, "market_sell_gui.yml"), "menus/market_sell_gui.yml");
        stackBuyConfig = loadAndSave(new File(folder, "stack_buy_gui.yml"), "menus/stack_buy_gui.yml");
        stackSellConfig = loadAndSave(new File(folder, "stack_sell_gui.yml"), "menus/stack_sell_gui.yml");
    }

    public Set<String> getCategories() {
        ConfigurationSection section = config.getConfigurationSection("items");
        return section != null ? section.getKeys(false) : Collections.emptySet();
    }

    private YamlConfiguration loadAndSave(File file, String res) {
        if (!file.exists()) plugin.saveResource(res, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    private String getCmd() { return plugin.getConfig().getString("commands.market.name", "rexashop"); }

    private String getDisplayName(String mat, String cat) {
        String path = "items." + cat + "." + mat + ".display_name";
        return config.contains(path) ? ChatColor.translateAlternateColorCodes('&', config.getString(path)) : mat.toLowerCase().replace("_", " ");
    }

    // GÜVENLİ MESAJ OKUYUCU: Mesaj yoksa veya YAML bozuksa varsayılanı gösterir
    private String getMsg(String path, String def) {
        String msg = plugin.getLanguageManager().getMessage(path);
        if (msg == null || msg.toLowerCase().contains("eksik") || msg.equals(path)) {
            return def;
        }
        return msg;
    }

    public void openMarket(Player player, String currentCat, int page) {
        player.removeMetadata("rexa_buying_mat", plugin);
        RexaMenu menu = new RexaMenu();
        Inventory inv = Bukkit.createInventory(menu, 54, "§8Market: " + currentCat);
        menu.setInventory(inv);

        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bm = border.getItemMeta(); bm.setDisplayName(" "); border.setItemMeta(bm);
        for (int i = 0; i < 54; i++) if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, border);

        ItemStack sun = new ItemStack(Material.SUNFLOWER);
        ItemMeta sm = sun.getItemMeta();
        
        String bName = getMsg("market.balance-info-name", "&6&lBakiye Bilgisi");
        String bLore = getMsg("market.balance-info-lore", "&7Bakiyeniz: &a%balance% Dinar");
        
        sm.setDisplayName(ChatColor.translateAlternateColorCodes('&', bName));
        sm.setLore(List.of(ChatColor.translateAlternateColorCodes('&', bLore.replace("%balance%", FormatUtils.format(plugin, plugin.getDatabaseManager().getBalance(player.getUniqueId().toString()))))));
        sun.setItemMeta(sm); inv.setItem(49, sun);

        ConfigurationSection section = config.getConfigurationSection("items." + currentCat);
        if (section != null) {
            List<String> keys = new ArrayList<>(section.getKeys(false));
            List<Integer> slots = new ArrayList<>();
            for (int r = 1; r < 5; r++) for (int c = 1; c < 8; c++) slots.add(r * 9 + c);

            int start = page * slots.size();
            for (int i = 0; i < slots.size() && (start + i) < keys.size(); i++) {
                String key = keys.get(start + i);
                Material mat = Material.matchMaterial(section.getString(key + ".material", "STONE"));
                ItemStack is = new ItemStack(mat);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§f" + getDisplayName(key, currentCat));
                
                double buyPrice = plugin.getPriceManager().getPrice(mat.name(), true);
                double sellPrice = plugin.getPriceManager().getPrice(mat.name(), false);
                
                List<String> lore = new ArrayList<>();
                List<String> leftClick = null;
                List<String> rightClick = null;

                // Alış Fiyatı
                if (buyPrice > 0) {
                    String buyLore = getMsg("market.buy-price-lore", "&7Alış Fiyatı: &a%price% Dinar");
                    lore.add(ChatColor.translateAlternateColorCodes('&', buyLore.replace("%price%", FormatUtils.formatPrice(buyPrice))));
                } else {
                    String noBuyLore = getMsg("market.cannot-buy-lore", "&7Alış Fiyatı: &cSatın Alınamaz");
                    lore.add(ChatColor.translateAlternateColorCodes('&', noBuyLore));
                }

                // Satış Fiyatı
                if (sellPrice > 0) {
                    String sellMsg = getMsg("market.sell-price-lore", "&7Satış Fiyatı: &e%price% Dinar");
                    lore.add(ChatColor.translateAlternateColorCodes('&', sellMsg.replace("%price%", FormatUtils.formatPrice(sellPrice))));
                } else {
                    String noSellLore = getMsg("market.cannot-sell-lore", "&7Satış Fiyatı: &cSatılamaz");
                    lore.add(ChatColor.translateAlternateColorCodes('&', noSellLore));
                }
                
                lore.add("");

                // Tıklama Yönergeleri
                if (buyPrice > 0) {
                    String leftClickMsg = getMsg("market.left-click-buy", "&eSatın almak için Sol Tıkla!");
                    lore.add(ChatColor.translateAlternateColorCodes('&', leftClickMsg));
                    leftClick = List.of("[console] " + getCmd() + " buygui " + mat.name() + " " + currentCat + " " + player.getName());
                }

                if (sellPrice > 0) {
                    String rightClickMsg = getMsg("market.right-click-sell", "&dSatmak için Sağ Tıkla!");
                    lore.add(ChatColor.translateAlternateColorCodes('&', rightClickMsg));
                    rightClick = List.of("[console] " + getCmd() + " sellgui " + mat.name() + " " + currentCat + " " + player.getName());
                }

                im.setLore(lore);
                is.setItemMeta(im);
                inv.setItem(slots.get(i), is);
                
                menu.addActions(slots.get(i), leftClick, rightClick);
            }

            String prevMsg = getMsg("market.previous-page", "&a<- Önceki");
            String nextMsg = getMsg("market.next-page", "&aSonraki ->");

            if (page > 0) createBtn(inv, 48, Material.ARROW, prevMsg, menu, "[console] " + getCmd() + " " + currentCat + " " + player.getName() + " " + (page - 1));
            if (keys.size() > (page + 1) * slots.size()) createBtn(inv, 50, Material.ARROW, nextMsg, menu, "[console] " + getCmd() + " " + currentCat + " " + player.getName() + " " + (page + 1));
        }
        player.openInventory(inv);
    }

    public void directBuy(Player player, String mat, int amount) {
        executeBuy(player, mat, amount, true);
    }

    public void confirmPurchase(Player player, String mat) {
        executeBuy(player, mat, selectedAmount.getOrDefault(player.getUniqueId(), 1), false);
    }

    private void executeBuy(Player player, String mat, int amount, boolean direct) {
        player.removeMetadata("rexa_buying_mat", plugin);
        String cat = player.hasMetadata("rexa_last_cat") ? player.getMetadata("rexa_last_cat").get(0).asString() : "blok";
        
        double unitPrice = plugin.getPriceManager().getPrice(mat, true);
        if (amount <= 0 || unitPrice <= 0) {
            player.closeInventory();
            player.sendMessage(plugin.getLanguageManager().getMessage("market.cannot-buy"));
            return;
        }

        double total = unitPrice * amount;
        double bal = plugin.getDatabaseManager().getBalance(player.getUniqueId().toString());

        if (bal >= total) {
            plugin.getDatabaseManager().setBalance(player.getUniqueId().toString(), bal - total);
            int rem = amount; 
            while (rem > 0) { 
                int add = Math.min(rem, 64); 
                player.getInventory().addItem(new ItemStack(Material.valueOf(mat), add)); 
                rem -= add; 
            }
            String msg = plugin.getLanguageManager().getMessage("market.buy-success")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%item%", getDisplayName(mat, cat))
                    .replace("%price%", FormatUtils.formatPrice(total));
            player.sendMessage(msg);
            player.closeInventory();
        } else { 
            player.sendMessage(plugin.getLanguageManager().getMessage("economy.insufficient-funds")); 
        }
    }

    public void openBuyGUI(Player player, String mat, String cat) {
        selectedAmount.put(player.getUniqueId(), 1);
        player.setMetadata("rexa_last_cat", new FixedMetadataValue(plugin, cat));
        player.setMetadata("rexa_buying_mat", new FixedMetadataValue(plugin, mat));
        refreshBuyGUI(player, mat, cat);
    }

    public void refreshBuyGUI(Player player, String mat, String cat) {
        RexaMenu menu = new RexaMenu();
        String title = buyConfig.getString("menu_title", "&8Satın Al: %item%").replace("%item%", getDisplayName(mat, cat));
        Inventory inv = Bukkit.createInventory(menu, buyConfig.getInt("size", 54), ChatColor.translateAlternateColorCodes('&', title));
        menu.setInventory(inv);

        Material borderMat = Material.matchMaterial(buyConfig.getString("border_material", "GRAY_STAINED_GLASS_PANE"));
        if (borderMat == null) borderMat = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack border = new ItemStack(borderMat);
        ItemMeta bm = border.getItemMeta(); bm.setDisplayName(" "); border.setItemMeta(bm);
        for (int s : buyConfig.getIntegerList("border_slots")) inv.setItem(s, border);

        int amount = selectedAmount.getOrDefault(player.getUniqueId(), 1);
        double unit = plugin.getPriceManager().getPrice(mat, true);

        ConfigurationSection items = buyConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection itemCfg = items.getConfigurationSection(key);
                if (itemCfg == null) continue;

                int slot = itemCfg.getInt("slot");
                if (key.equals("display")) {
                    ItemStack d = new ItemStack(Material.valueOf(mat), Math.max(1, Math.min(64, amount)));
                    ItemMeta dm = d.getItemMeta(); 
                    dm.setDisplayName("§f" + getDisplayName(mat, cat));
                    List<String> lore = new ArrayList<>();
                    for (String l : itemCfg.getStringList("lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', l
                            .replace("%unit_price%", FormatUtils.formatPrice(unit))
                            .replace("%total_price%", FormatUtils.formatPrice(unit * amount))
                        ));
                    }
                    dm.setLore(lore);
                    d.setItemMeta(dm);
                    inv.setItem(slot, d);
                    continue;
                }

                Material m = Material.matchMaterial(itemCfg.getString("material", "STONE"));
                if (m == null) m = Material.STONE;
                String name = itemCfg.getString("display", "");
                List<String> loreTemplate = itemCfg.getStringList("lore");
                List<String> lore = new ArrayList<>();
                if (loreTemplate != null) {
                    for (String l : loreTemplate) lore.add(ChatColor.translateAlternateColorCodes('&', l));
                }

                ItemStack btn = new ItemStack(m);
                ItemMeta btnMeta = btn.getItemMeta();
                btnMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                if (!lore.isEmpty()) btnMeta.setLore(lore);
                btn.setItemMeta(btnMeta);
                inv.setItem(slot, btn);

                String cmd = null;
                if (key.startsWith("adjust_")) {
                    cmd = "[console] " + getCmd() + " adjust " + mat + " " + itemCfg.getInt("amount", 0) + " " + cat + " " + player.getName();
                } else if (key.equals("stack_mode")) {
                    cmd = "[console] " + getCmd() + " stackgui " + mat + " " + cat + " " + player.getName();
                } else if (key.equals("confirm")) {
                    cmd = "[console] " + getCmd() + " confirm " + mat + " " + player.getName();
                } else if (key.equals("back")) {
                    cmd = "[console] " + getCmd() + " " + cat + " " + player.getName() + " 0";
                }
                if (cmd != null) menu.addActions(slot, List.of(cmd), null);
            }
        }
        player.openInventory(inv);
    }

    public void openStackBuyGUI(Player player, String mat, String cat) {
        RexaMenu menu = new RexaMenu();
        String title = stackBuyConfig.getString("menu_title", "&dPaket Al: %item%").replace("%item%", getDisplayName(mat, cat));
        Inventory inv = Bukkit.createInventory(menu, stackBuyConfig.getInt("size", 9), ChatColor.translateAlternateColorCodes('&', title));
        menu.setInventory(inv);

        double unitPrice = plugin.getPriceManager().getPrice(mat, true);

        ConfigurationSection items = stackBuyConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection itemCfg = items.getConfigurationSection(key);
                if (itemCfg == null) continue;

                int slot = itemCfg.getInt("slot");
                Material m = Material.matchMaterial(itemCfg.getString("material", mat));
                if (m == null || m == Material.AIR) m = Material.valueOf(mat);

                int guiAmount = itemCfg.getInt("amount", 1);
                
                ItemStack btn = new ItemStack(m, Math.max(1, Math.min(64, guiAmount)));
                ItemMeta meta = btn.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemCfg.getString("display", "")));

                if (key.startsWith("stack_")) {
                    int stacks = itemCfg.getInt("amount", 1);
                    double totalPrice = unitPrice * stacks * 64;
                    
                    meta.setLore(List.of(
                        "§7Toplam Fiyat: §a" + FormatUtils.formatPrice(totalPrice) + " Dinar"
                    ));
                    
                    menu.addActions(slot, List.of("[console] " + getCmd() + " directbuy " + mat + " " + (stacks * 64) + " " + player.getName()), null);
                } else if (key.equals("cancel")) {
                    menu.addActions(slot, List.of("[console] " + getCmd() + " buygui " + mat + " " + cat + " " + player.getName()), null);
                }
                
                btn.setItemMeta(meta);
                inv.setItem(slot, btn);
            }
        }
        player.openInventory(inv);
    }

    public void adjustAmount(Player player, String mat, int ch, String cat) {
        selectedAmount.put(player.getUniqueId(), Math.max(1, Math.min(64, selectedAmount.getOrDefault(player.getUniqueId(), 1) + ch)));
        refreshBuyGUI(player, mat, cat);
    }

    public void directSell(Player player, String mat, int amount) {
        selectedAmount.put(player.getUniqueId(), amount);
        confirmSell(player, mat);
    }

    public void openSellGUI(Player player, String mat, String cat) {
        selectedAmount.put(player.getUniqueId(), 1);
        player.setMetadata("rexa_last_cat", new FixedMetadataValue(plugin, cat));
        player.setMetadata("rexa_buying_mat", new FixedMetadataValue(plugin, mat)); 
        refreshSellGUI(player, mat, cat);
    }

    public void refreshSellGUI(Player player, String mat, String cat) {
        RexaMenu menu = new RexaMenu();
        String title = sellConfig.getString("menu_title", "&8Sat: %item%").replace("%item%", getDisplayName(mat, cat));
        Inventory inv = Bukkit.createInventory(menu, sellConfig.getInt("size", 54), ChatColor.translateAlternateColorCodes('&', title));
        menu.setInventory(inv);

        Material borderMat = Material.matchMaterial(sellConfig.getString("border_material", "GRAY_STAINED_GLASS_PANE"));
        if (borderMat == null) borderMat = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack border = new ItemStack(borderMat);
        ItemMeta bm = border.getItemMeta(); bm.setDisplayName(" "); border.setItemMeta(bm);
        for (int s : sellConfig.getIntegerList("border_slots")) inv.setItem(s, border);

        int amount = selectedAmount.getOrDefault(player.getUniqueId(), 1);
        double unit = plugin.getPriceManager().getPrice(mat, false);

        ConfigurationSection items = sellConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection itemCfg = items.getConfigurationSection(key);
                if (itemCfg == null) continue;

                int slot = itemCfg.getInt("slot");
                if (key.equals("display")) {
                    ItemStack d = new ItemStack(Material.valueOf(mat), Math.max(1, Math.min(64, amount)));
                    ItemMeta dm = d.getItemMeta(); 
                    dm.setDisplayName("§f" + getDisplayName(mat, cat));
                    List<String> lore = new ArrayList<>();
                    for (String l : itemCfg.getStringList("lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', l
                            .replace("%unit_price%", FormatUtils.formatPrice(unit))
                            .replace("%total_price%", FormatUtils.formatPrice(unit * amount))
                        ));
                    }
                    dm.setLore(lore);
                    d.setItemMeta(dm);
                    inv.setItem(slot, d);
                    continue;
                }

                Material m = Material.matchMaterial(itemCfg.getString("material", "STONE"));
                if (m == null) m = Material.STONE;
                String name = itemCfg.getString("display", "");
                List<String> loreTemplate = itemCfg.getStringList("lore");
                List<String> lore = new ArrayList<>();
                if (loreTemplate != null) {
                    for (String l : loreTemplate) lore.add(ChatColor.translateAlternateColorCodes('&', l));
                }

                ItemStack btn = new ItemStack(m);
                ItemMeta btnMeta = btn.getItemMeta();
                btnMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                if (!lore.isEmpty()) btnMeta.setLore(lore);
                btn.setItemMeta(btnMeta);
                inv.setItem(slot, btn);

                String cmd = null;
                if (key.startsWith("adjust_")) {
                    cmd = "[console] " + getCmd() + " adjustsell " + mat + " " + itemCfg.getInt("amount", 0) + " " + cat + " " + player.getName();
                } else if (key.equals("stack_mode")) {
                    cmd = "[console] " + getCmd() + " stacksellgui " + mat + " " + cat + " " + player.getName();
                } else if (key.equals("confirm")) {
                    cmd = "[console] " + getCmd() + " confirmsell " + mat + " " + player.getName();
                } else if (key.equals("back")) {
                    cmd = "[console] " + getCmd() + " " + cat + " " + player.getName() + " 0";
                }
                if (cmd != null) menu.addActions(slot, List.of(cmd), null);
            }
        }
        player.openInventory(inv);
    }

    public void openStackSellGUI(Player player, String mat, String cat) {
        RexaMenu menu = new RexaMenu();
        String title = stackSellConfig.getString("menu_title", "&dPaket Sat: %item%").replace("%item%", getDisplayName(mat, cat));
        Inventory inv = Bukkit.createInventory(menu, stackSellConfig.getInt("size", 9), ChatColor.translateAlternateColorCodes('&', title));
        menu.setInventory(inv);

        double unitPrice = plugin.getPriceManager().getPrice(mat, false);

        ConfigurationSection items = stackSellConfig.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection itemCfg = items.getConfigurationSection(key);
                if (itemCfg == null) continue;

                int slot = itemCfg.getInt("slot");
                Material m = Material.matchMaterial(itemCfg.getString("material", mat));
                if (m == null || m == Material.AIR) m = Material.valueOf(mat);

                int guiAmount = itemCfg.getInt("amount", 1);
                
                ItemStack btn = new ItemStack(m, Math.max(1, Math.min(64, guiAmount)));
                ItemMeta meta = btn.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemCfg.getString("display", "")));

                if (key.startsWith("stack_")) {
                    int stacks = itemCfg.getInt("amount", 1);
                    double totalPrice = unitPrice * stacks * 64;
                    
                    meta.setLore(List.of(
                        "§7Toplam Kazanç: §6" + FormatUtils.formatPrice(totalPrice) + " Dinar"
                    ));
                    
                    menu.addActions(slot, List.of("[console] " + getCmd() + " directsell " + mat + " " + (stacks * 64) + " " + player.getName()), null);
                } else if (key.equals("cancel")) {
                    menu.addActions(slot, List.of("[console] " + getCmd() + " sellgui " + mat + " " + cat + " " + player.getName()), null);
                }
                
                btn.setItemMeta(meta);
                inv.setItem(slot, btn);
            }
        }
        player.openInventory(inv);
    }

    public void adjustSellAmount(Player player, String mat, int ch, String cat) {
        selectedAmount.put(player.getUniqueId(), Math.max(1, Math.min(64, selectedAmount.getOrDefault(player.getUniqueId(), 1) + ch)));
        refreshSellGUI(player, mat, cat);
    }

    public void confirmSell(Player player, String mat) {
        int amount = selectedAmount.getOrDefault(player.getUniqueId(), 1);
        double unitPrice = plugin.getPriceManager().getPrice(mat, false);
        
        if (amount <= 0 || unitPrice <= 0) {
            player.closeInventory();
            return;
        }

        String cat = player.hasMetadata("rexa_last_cat") ? player.getMetadata("rexa_last_cat").get(0).asString() : "blok";
        Material material = Material.valueOf(mat);
        int playerAmount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                playerAmount += item.getAmount();
            }
        }

        if (playerAmount >= amount) {
            int rem = amount;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() == material) {
                    if (item.getAmount() <= rem) {
                        rem -= item.getAmount();
                        player.getInventory().setItem(i, null);
                    } else {
                        item.setAmount(item.getAmount() - rem);
                        rem = 0;
                    }
                    if (rem <= 0) break;
                }
            }

            double total = unitPrice * amount;
            double bal = plugin.getDatabaseManager().getBalance(player.getUniqueId().toString());
            plugin.getDatabaseManager().setBalance(player.getUniqueId().toString(), bal + total);

            String msg = plugin.getLanguageManager().getMessage("market.sell-success")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%item%", getDisplayName(mat, cat))
                    .replace("%price%", FormatUtils.formatPrice(total));
            player.sendMessage(msg);
            
            player.closeInventory();
        } else {
            player.sendMessage("§cEnvanterinizde satmak için yeterli eşya yok!");
        }
    }

    private void createBtn(Inventory inv, int s, Material m, String n, RexaMenu menu, String c) {
        ItemStack i = new ItemStack(m); ItemMeta im = i.getItemMeta(); im.setDisplayName(ChatColor.translateAlternateColorCodes('&', n)); i.setItemMeta(im);
        inv.setItem(s, i); menu.addActions(s, List.of(c), null);
    }
}