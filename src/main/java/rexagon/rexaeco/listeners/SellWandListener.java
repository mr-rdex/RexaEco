package rexagon.rexaeco.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

public class SellWandListener implements Listener {
    private final RexaEco plugin;

    public SellWandListener(RexaEco plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        
        if (item == null || !item.hasItemMeta()) return;
        
        // Elindeki eşyanın bizim "Satış Çubuğumuz" olup olmadığını kontrol ediyoruz
        NamespacedKey key = new NamespacedKey(plugin, "sell_wand");
        if (!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;
        
        Block block = e.getClickedBlock();
        // Tıklanılan şey bir sandık, tuzaklı sandık, fıçı veya shulker kutusu değilse iptal et
        if (block == null || !(block.getState() instanceof Container)) return;
        
        e.setCancelled(true); // Çubukla tıklayınca sandık menüsünün açılmasını engeller
        
        Container container = (Container) block.getState();
        Inventory inv = container.getInventory();
        
        double totalEarned = 0;
        int itemsSold = 0;
        
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack slotItem = inv.getItem(i);
            if (slotItem == null || slotItem.getType() == Material.AIR) continue;
            
            double price = plugin.getPriceManager().getPrice(slotItem.getType().name(), false);
            if (price > 0) {
                totalEarned += (price * slotItem.getAmount());
                itemsSold += slotItem.getAmount();
                inv.setItem(i, null); // Eşyayı sandıktan sil
            }
        }
        
        if (totalEarned > 0) {
            String uuid = p.getUniqueId().toString();
            double currentBal = plugin.getDatabaseManager().getBalance(uuid);
            plugin.getDatabaseManager().setBalance(uuid, currentBal + totalEarned);
            
            // FormatUtils.formatPrice ile küsuratlı fiyat gösterimi
            String msg = plugin.getLanguageManager().getMessage("sell-wand.sold")
                    .replace("%amount%", String.valueOf(itemsSold))
                    .replace("%price%", FormatUtils.formatPrice(totalEarned));
            p.sendMessage(msg);
        } else {
            p.sendMessage(plugin.getLanguageManager().getMessage("sell-wand.no-items"));
        }
    }
}