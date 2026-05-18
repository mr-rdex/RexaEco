package rexagon.rexaeco.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import rexagon.rexaeco.RexaEco;

import java.util.List;

public class MenuListener implements Listener {
    private final RexaEco plugin;

    public MenuListener(RexaEco plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof RexaMenu menu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        List<String> actions = event.isRightClick() ? menu.getRightClickCommands(event.getSlot()) : menu.getLeftClickCommands(event.getSlot());
        
        if (actions != null) {
            for (String action : actions) {
                executeAction(player, action);
            }
        }
    }

    private void executeAction(Player player, String action) {
        if (action.startsWith("[close]")) {
            player.closeInventory();
        } else if (action.startsWith("[message] ")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', action.substring(10)));
        } else if (action.startsWith("[player] ")) {
            player.chat("/" + action.substring(9));
        } else if (action.startsWith("[console] ")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.substring(10).replace("%player%", player.getName()));
        }
    }
}