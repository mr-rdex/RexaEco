package rexagon.rexaeco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;

import java.util.Collections;
import java.util.List;

public class SellCommand extends Command {
    private final RexaEco plugin;

    public SellCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Eşya satış menüsünü açar", "/" + name, aliases);
        this.plugin = plugin;
        this.setPermission("rexaeco.sell"); // Kilit Eklendi
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("only-players"));
            return true;
        }

        if (!player.hasPermission(this.getPermission())) {
            player.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        plugin.getSellMenuListener().openSellMenu(player);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}