package rexagon.rexaeco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BankCommand extends Command {
    private final RexaEco plugin;

    public BankCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Banka işlemleri", "/" + name, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!plugin.getPermissionManager().has(player, "commands.bank.bank-command")) return true;

        String uuid = player.getUniqueId().toString();

        if (args.length == 0) {
            plugin.getMenuManager().openMenu(player, "bank");
            return true;
        }

        if (args.length == 2) {
            String action = args[0].toLowerCase();
            double amount;
            try { amount = Double.parseDouble(args[1]); } catch (NumberFormatException e) { return true; }

            double currentWallet = plugin.getDatabaseManager().getBalance(uuid);
            double currentBank = plugin.getDatabaseManager().getBankBalance(uuid);

            if (action.equals("yatir")) {
                if (currentWallet < amount) {
                    player.sendMessage(plugin.getLanguageManager().getMessage("economy.insufficient-funds"));
                    return true;
                }
                plugin.getDatabaseManager().setBalance(uuid, currentWallet - amount);
                plugin.getDatabaseManager().setBankBalance(uuid, currentBank + amount);
                player.sendMessage(plugin.getLanguageManager().getMessage("bank.balance")
                        .replace("%amount%", FormatUtils.format(plugin, currentBank + amount)));
            } else if (action.equals("cek")) {
                if (currentBank < amount) {
                    player.sendMessage(plugin.getLanguageManager().getMessage("economy.insufficient-funds"));
                    return true;
                }
                plugin.getDatabaseManager().setBankBalance(uuid, currentBank - amount);
                plugin.getDatabaseManager().setBalance(uuid, currentWallet + amount);
                player.sendMessage(plugin.getLanguageManager().getMessage("economy.balance")
                        .replace("%amount%", FormatUtils.format(plugin, currentWallet + amount)));
            }
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("yatir", "cek").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        return Collections.emptyList();
    }
}