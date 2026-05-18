package rexagon.rexaeco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;

import java.util.Collections;
import java.util.List;

public class BlackMarketCommand extends Command {
    private final RexaEco plugin;

    public BlackMarketCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Kara Borsa menüsünü açar", "/" + name, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (!player.hasPermission("rexa.blackmarket.use")) {
            player.sendMessage("§cBu komutu kullanmak için yetkiniz yok.");
            return true;
        }

        plugin.getBlackMarketManager().openMenu(player);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}