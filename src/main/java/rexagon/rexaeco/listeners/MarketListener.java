package rexagon.rexaeco.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import rexagon.rexaeco.RexaEco;

public class MarketListener implements Listener {
    private final RexaEco plugin;

    public MarketListener(RexaEco plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        
        String title = event.getView().getTitle();

        // 1. DURUM: Satın alma veya Satma menüsündeyken ESC'ye basılırsa
        if (player.hasMetadata("rexa_buying_mat")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Eğer oyuncu başka bir GUI açmadıysa (gerçekten ESC yaptıysa)
                if (player.getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING) {
                    if (player.hasMetadata("rexa_last_cat")) {
                        String lastCat = player.getMetadata("rexa_last_cat").get(0).asString();
                        plugin.getMarketManager().openMarket(player, lastCat, 0); // Kategoriye geri yolla
                    }
                }
            }, 1L);
            return;
        }

        // 2. DURUM: Kategori menüsündeyken (rshop blok vb.) ESC'ye basılırsa
        if (title.startsWith("§8Market: ")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING) {
                    player.performCommand("market"); // /market komutunu çalıştır
                }
            }, 1L);
        }
    }
}