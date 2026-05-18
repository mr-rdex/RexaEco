package rexagon.rexaeco.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import rexagon.rexaeco.RexaEco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarketCommand extends Command {
    private final RexaEco plugin;

    public MarketCommand(String name, List<String> aliases, RexaEco plugin) {
        super(name, "RexaShop Market Sistemi", "/" + name, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player p) plugin.getMarketManager().openMarket(p, "blok", 0);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (isInternalAction(sub)) {
            handleInternal(sub, args);
            return true;
        }

        String category;
        Player target;
        int page = 0;

        if (sub.equals("open") && args.length >= 3) {
            category = args[1];
            target = Bukkit.getPlayer(args[2]);
            if (args.length > 3) try { page = Integer.parseInt(args[3]); } catch (Exception ignored) {}
        } else {
            category = sub;
            target = (args.length > 1) ? Bukkit.getPlayer(args[1]) : (sender instanceof Player ? (Player) sender : null);
            if (args.length > 2) try { page = Integer.parseInt(args[2]); } catch (Exception ignored) {}
        }

        if (target != null) {
            plugin.getMarketManager().openMarket(target, category, page);
        } else {
            sender.sendMessage("§cKullanım: /" + label + " <kategori> <oyuncu>");
        }
        return true;
    }

    private boolean isInternalAction(String sub) {
        return List.of("buygui", "stackgui", "confirm", "directbuy", "adjust", "sellgui", "stacksellgui", "adjustsell", "confirmsell", "directsell").contains(sub);
    }

    private void handleInternal(String sub, String[] args) {
        try {
            switch (sub) {
                case "buygui" -> { Player t = Bukkit.getPlayer(args[3]); if(t != null) plugin.getMarketManager().openBuyGUI(t, args[1], args[2]); }
                case "stackgui" -> { Player t = Bukkit.getPlayer(args[3]); if(t != null) plugin.getMarketManager().openStackBuyGUI(t, args[1], args[2]); }
                case "adjust" -> { Player t = Bukkit.getPlayer(args[4]); if(t != null) plugin.getMarketManager().adjustAmount(t, args[1], Integer.parseInt(args[2]), args[3]); }
                case "confirm" -> { Player t = Bukkit.getPlayer(args[2]); if(t != null) plugin.getMarketManager().confirmPurchase(t, args[1]); }
                case "directbuy" -> { Player t = Bukkit.getPlayer(args[3]); if(t != null) plugin.getMarketManager().directBuy(t, args[1], Integer.parseInt(args[2])); }
                case "sellgui" -> { Player t = Bukkit.getPlayer(args[3]); if(t != null) plugin.getMarketManager().openSellGUI(t, args[1], args[2]); }
                case "stacksellgui" -> { Player t = Bukkit.getPlayer(args[3]); if(t != null) plugin.getMarketManager().openStackSellGUI(t, args[1], args[2]); }
                case "adjustsell" -> { Player t = Bukkit.getPlayer(args[4]); if(t != null) plugin.getMarketManager().adjustSellAmount(t, args[1], Integer.parseInt(args[2]), args[3]); }
                case "confirmsell" -> { Player t = Bukkit.getPlayer(args[2]); if(t != null) plugin.getMarketManager().confirmSell(t, args[1]); }
                case "directsell" -> { Player t = Bukkit.getPlayer(args[3]); if(t != null) plugin.getMarketManager().directSell(t, args[1], Integer.parseInt(args[2])); }
            }
        } catch (Exception ignored) {}
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> categories = new ArrayList<>(plugin.getMarketManager().getCategories());
            StringUtil.copyPartialMatches(args[0], categories, completions);
        } else if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) players.add(p.getName());
            StringUtil.copyPartialMatches(args[1], players, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}