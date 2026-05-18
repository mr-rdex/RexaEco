package rexagon.rexaeco.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import rexagon.rexaeco.RexaEco;
import rexagon.rexaeco.utils.FormatUtils;

import java.util.List;

public class PayCommand extends org.bukkit.command.Command {

    private final RexaEco plugin;

    public PayCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name);
        this.setAliases(aliases);
        this.plugin = plugin;
        this.setPermission("rexaeco.pay"); // Kilit Eklendi
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("only-players"));
            return true;
        }

        if (!player.hasPermission(this.getPermission())) {
            player.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getLanguageManager().getMessage("pay-system.usage"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(plugin.getLanguageManager().getMessage("pay-system.player-not-found"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getLanguageManager().getMessage("pay-system.cannot-pay-self"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getLanguageManager().getMessage("pay-system.invalid-amount"));
            return true;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return true;
        Economy eco = rsp.getProvider();

        if (eco.getBalance(player) < amount) {
            player.sendMessage(plugin.getLanguageManager().getMessage("pay-system.not-enough")
                    .replace("%current%", FormatUtils.format(plugin, eco.getBalance(player))));
            return true;
        }

        eco.withdrawPlayer(player, amount);
        eco.depositPlayer(target, amount);

        String formattedAmount = FormatUtils.format(plugin, amount);
        String targetName = target.getName() != null ? target.getName() : args[0];

        player.sendMessage(plugin.getLanguageManager().getMessage("pay-system.sent")
                .replace("%amount%", formattedAmount)
                .replace("%player%", targetName));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(plugin.getLanguageManager().getMessage("pay-system.received")
                    .replace("%amount%", formattedAmount)
                    .replace("%player%", player.getName()));
        }

        return true;
    }
}