package rexagon.rexaeco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.util.Collections;
import java.util.List;

public class MoneyCommand extends Command {
    private final RexaEco plugin;

    public MoneyCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Bakiye gösterir", "/" + name, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!plugin.getPermissionManager().has(player, "commands.balance.own")) return true;

        double balance = plugin.getDatabaseManager().getBalance(player.getUniqueId().toString());
        player.sendMessage(plugin.getLanguageManager().getMessage("economy.balance")
                .replace("%amount%", FormatUtils.format(plugin, balance)));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}