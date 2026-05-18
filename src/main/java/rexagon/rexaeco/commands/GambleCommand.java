package rexagon.rexaeco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;

import java.util.Collections;
import java.util.List;

public class GambleCommand extends Command {
    private final RexaEco plugin;

    public GambleCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Kumar oyna", "/" + name + " <miktar>", aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage("§eKullanım: /" + label + " <miktar>");
            return true;
        }

        try {
            double amount = Double.parseDouble(args[0]);
            // BURASI GÜNCELLENDİ: play yerine openSlotGUI çağırılıyor
            plugin.getGambleManager().openSlotGUI(player, amount);
        } catch (NumberFormatException e) {
            player.sendMessage("§cGeçerli bir miktar girin.");
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}