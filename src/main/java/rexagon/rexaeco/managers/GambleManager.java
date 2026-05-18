package rexagon.rexaeco.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.menus.RexaMenu;

import java.util.Random;

public class GambleManager {
    private final RexaEco plugin;
    private final Material[] icons = {Material.DIAMOND, Material.GOLD_INGOT, Material.EMERALD, Material.APPLE, Material.COAL};

    public GambleManager(RexaEco plugin) { this.plugin = plugin; }

    public void openSlotGUI(Player player, double bet) {
        if (plugin.getDatabaseManager().getBalance(player.getUniqueId().toString()) < bet) {
            player.sendMessage("§cYetersiz bakiye!");
            return;
        }

        RexaMenu rexaMenu = new RexaMenu();
        Inventory inv = Bukkit.createInventory(rexaMenu, 27, "§8Slot Makinesi");
        rexaMenu.setInventory(inv);
        player.openInventory(inv);

        new BukkitRunnable() {
            int ticks = 0;
            final Random random = new Random();

            @Override
            public void run() {
                if (ticks >= 20) {
                    this.cancel();
                    calculateResult(player, inv, bet);
                    return;
                }

                // Dönen animasyon slotları: 11, 13, 15
                inv.setItem(11, new ItemStack(icons[random.nextInt(icons.length)]));
                inv.setItem(13, new ItemStack(icons[random.nextInt(icons.length)]));
                inv.setItem(15, new ItemStack(icons[random.nextInt(icons.length)]));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }

    private void calculateResult(Player player, Inventory inv, double bet) {
        Material m1 = inv.getItem(11).getType();
        Material m2 = inv.getItem(13).getType();
        Material m3 = inv.getItem(15).getType();

        double winMultiplier = 0;
        if (m1 == m2 && m2 == m3) winMultiplier = 5;
        else if (m1 == m2 || m2 == m3 || m1 == m3) winMultiplier = 1.5;

        double currentBal = plugin.getDatabaseManager().getBalance(player.getUniqueId().toString());
        plugin.getDatabaseManager().setBalance(player.getUniqueId().toString(), (currentBal - bet) + (bet * winMultiplier));

        if (winMultiplier > 1) {
            player.sendMessage("§a§lTEBRİKLER! §e" + (bet * winMultiplier) + " Coin kazandınız!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            player.sendMessage("§cKaybettiniz...");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }
}