package rexagon.rexaeco.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;

import java.util.ArrayList;
import java.util.List;

public class SellWandCommand extends Command {
    private final RexaEco plugin;

    public SellWandCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Satış Çubuğu verir", "/" + name + " <kullanıcıadı>", aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() && !sender.hasPermission("rexaeco.admin.sellwand")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cKullanım: /" + label + " <KullanıcıAdı>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("sell-wand.player-not-found"));
            return true;
        }

        ItemStack wand = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("§dѕᴀᴛɪş çᴜʙᴜɢᴜ");
        
        List<String> lore = new ArrayList<>();
        lore.add("§7");
        lore.add("§7ʙɪʀ ѕᴀɴᴅɪɢᴀ ᴠᴇʏᴀ ꜰɪçɪʏᴀ");
        lore.add("§7ѕᴀɢ ᴛɪᴋʟᴀᴅɪɢɪɴᴅᴀ ɪçɪɴᴅᴇᴋɪ");
        lore.add("§7ᴇşʏᴀʟᴀʀɪ ᴏᴛᴏᴍᴀᴛɪᴋ ѕᴀᴛᴀʀ!");
        meta.setLore(lore);

        NamespacedKey key = new NamespacedKey(plugin, "sell_wand");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        wand.setItemMeta(meta);

        target.getInventory().addItem(wand);
        target.sendMessage(plugin.getLanguageManager().getMessage("sell-wand.received"));

        if (!sender.equals(target)) {
            String msg = plugin.getLanguageManager().getMessage("sell-wand.given").replace("%player%", target.getName());
            sender.sendMessage(msg);
        }

        return true;
    }
}