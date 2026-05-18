package rexagon.rexaeco.listeners;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

public class MoneyVoucherListener implements Listener {

    private final RexaEco plugin;

    public MoneyVoucherListener(RexaEco plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) return;

        NamespacedKey moneyKey = new NamespacedKey(plugin, "money_amount");

        if (item.getItemMeta().getPersistentDataContainer().has(moneyKey, PersistentDataType.DOUBLE)) {
            event.setCancelled(true); 

            double amount = item.getItemMeta().getPersistentDataContainer().get(moneyKey, PersistentDataType.DOUBLE);
            
            item.setAmount(item.getAmount() - 1);

            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                rsp.getProvider().depositPlayer(player, amount);
                player.sendMessage(plugin.getLanguageManager().getMessage("money-system.redeemed")
                        .replace("%amount%", FormatUtils.format(plugin, amount)));
            }
        }
    }
}