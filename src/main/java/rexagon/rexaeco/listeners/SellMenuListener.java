package rexagon.rexaeco.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellMenuListener implements Listener {
    private final RexaEco plugin;
    private YamlConfiguration config;

    public SellMenuListener(RexaEco plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "menus/sell_gui.yml");
        if (!file.exists()) plugin.saveResource("menus/sell_gui.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void openSellMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, config.getInt("size"), config.getString("menu_title").replace("&", "§"));
        
        ItemStack border = new ItemStack(Material.matchMaterial(config.getString("border_material")));
        ItemMeta bm = border.getItemMeta(); bm.setDisplayName(" "); border.setItemMeta(bm);
        for (int s : config.getIntegerList("border_slots")) inv.setItem(s, border);

        updateInfoItem(inv, 0);
        player.openInventory(inv);
    }

    private void updateInfoItem(Inventory inv, double total) {
        int slot = config.getInt("info_item.slot");
        ItemStack info = new ItemStack(Material.matchMaterial(config.getString("info_item.material")));
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName(config.getString("info_item.display").replace("&", "§"));
        
        List<String> lore = new ArrayList<>();
        if (total <= 0) {
            for(String s : config.getStringList("info_item.lore_empty")) lore.add(s.replace("&", "§"));
        } else {
            for(String s : config.getStringList("info_item.lore_filled")) {
                // KÜSURAT DÜZELTMESİ: FormatUtils.formatPrice kullanıldı
                lore.add(s.replace("&", "§").replace("%total_price%", FormatUtils.formatPrice(total)));
            }
        }
        meta.setLore(lore); info.setItemMeta(meta); inv.setItem(slot, info);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(config.getString("menu_title").replace("&", "§"))) return;
        
        int slot = e.getRawSlot();
        List<Integer> allowed = config.getIntegerList("input_slots");

        if (slot < e.getInventory().getSize() && !allowed.contains(slot)) {
            e.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double currentTotal = 0;
            for (int s : allowed) {
                ItemStack item = e.getInventory().getItem(s);
                if (item != null && item.getType() != Material.AIR) {
                    double price = plugin.getPriceManager().getPrice(item.getType().name(), false);
                    if (price > 0) {
                        currentTotal += (price * item.getAmount());
                    }
                }
            }
            updateInfoItem(e.getInventory(), currentTotal);
        }, 1L);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(config.getString("menu_title").replace("&", "§"))) return;
        Player p = (Player) e.getPlayer();
        double totalToPay = 0;
        boolean hasUnsoldItems = false;
        List<Integer> allowed = config.getIntegerList("input_slots");

        for (int s : allowed) {
            ItemStack item = e.getInventory().getItem(s);
            if (item == null || item.getType() == Material.AIR) continue;

            double unitPrice = plugin.getPriceManager().getPrice(item.getType().name(), false);

            if (unitPrice > 0) {
                // EŞYA DEĞERLİ: Satın al
                totalToPay += (unitPrice * item.getAmount());
                e.getInventory().setItem(s, null); // Menüden temizle
            } else {
                // EŞYA DEĞERSİZ: İade et
                hasUnsoldItems = true;
                Map<Integer, ItemStack> drop = p.getInventory().addItem(item);
                // Eğer envanter doluysa yere at
                if (!drop.isEmpty()) {
                    for (ItemStack left : drop.values()) {
                        p.getWorld().dropItemNaturally(p.getLocation(), left);
                    }
                }
                e.getInventory().setItem(s, null); // Menüden temizle
            }
        }

        // KÜSURAT DÜZELTMESİ: FormatUtils.formatPrice kullanıldı
        if (totalToPay > 0) {
            plugin.getDatabaseManager().setBalance(p.getUniqueId().toString(), 
                plugin.getDatabaseManager().getBalance(p.getUniqueId().toString()) + totalToPay);
            
            String successMsg = plugin.getLanguageManager().getMessage("sell-menu.sell-success")
                    .replace("%amount%", FormatUtils.formatPrice(totalToPay));
            p.sendMessage(successMsg);
        }

        if (hasUnsoldItems) {
            p.sendMessage(plugin.getLanguageManager().getMessage("sell-menu.items-returned"));
        }
    }
}