package rexagon.rexaeco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;
import java.util.List;

public class SeasonCommand extends Command {
    private final RexaEco plugin;

    public SeasonCommand(RexaEco plugin) {
        super("sezonayarla", "Sezon ayarlar", "/sezonayarla <SPRING|SUMMER|FALL|WINTER|RESET>", List.of("rexaeco-season"));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("rexaeco.admin.reload")) return true;
        if (args.length == 1) {
            String choice = args[0].toUpperCase();
            if (choice.equals("RESET")) plugin.getPriceManager().setManualSeason(null);
            else plugin.getPriceManager().setManualSeason(choice);
            sender.sendMessage("§aSezon ayarlandı: §e" + choice);
            return true;
        }
        return false;
    }
}