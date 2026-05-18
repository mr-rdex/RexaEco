package rexagon.rexaeco.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EcoCommand extends Command {
    private final RexaEco plugin;

    public EcoCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "Admin ekonomi ve sistem komutları", "/" + name, aliases);
        this.setPermission("rexa.admin");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("rexa.admin")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.help").replace("%cmd%", commandLabel));
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            if (!sender.hasPermission("rexaeco.admin.reload")) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                return true;
            }
            plugin.reloadPlugin();
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.reload"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.usage")
                    .replace("%cmd%", commandLabel)
                    .replace("%action%", action));
            return true;
        }

        if (action.equals("upgradebank")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                plugin.getBankManager().upgradeBank(target);
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.bank-upgraded")
                        .replace("%player%", target.getName()));
            } else {
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.player-not-found"));
            }
            return true;
        } 
        
        if (action.equals("claiminterest")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                plugin.getBankManager().claimInterest(target);
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.bank-interest")
                        .replace("%player%", target.getName()));
            } else {
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.player-not-found"));
            }
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.need-amount"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        double amount;

        try { 
            amount = Double.parseDouble(args[2]); 
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.invalid-amount"));
            return true;
        }

        String uuid = target.getUniqueId().toString();
        
        if (!plugin.getDatabaseManager().hasAccount(uuid)) {
            String targetName = target.getName() != null ? target.getName() : args[1];
            plugin.getDatabaseManager().createAccount(uuid, targetName);
        }

        double currentBalance = plugin.getDatabaseManager().getBalance(uuid);
        String targetName = target.getName() != null ? target.getName() : args[1];

        // FormatUtils entegrasyonu
        String formattedAmount = FormatUtils.format(plugin, amount);

        switch (action) {
            case "give" -> {
                plugin.getDatabaseManager().setBalance(uuid, currentBalance + amount);
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.money-given")
                        .replace("%player%", targetName)
                        .replace("%amount%", formattedAmount));
            }
            case "take" -> {
                plugin.getDatabaseManager().setBalance(uuid, Math.max(0, currentBalance - amount));
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.money-taken")
                        .replace("%player%", targetName)
                        .replace("%amount%", formattedAmount));
            }
            case "set" -> {
                plugin.getDatabaseManager().setBalance(uuid, amount);
                sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.money-set")
                        .replace("%player%", targetName)
                        .replace("%amount%", formattedAmount));
            }
            default -> sender.sendMessage(plugin.getLanguageManager().getMessage("admin-command.invalid-action"));
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "take", "set", "reload", "upgradebank", "claiminterest").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reload")) return new ArrayList<>();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}