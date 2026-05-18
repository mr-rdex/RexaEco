package rexagon.rexaeco.utils;

import org.bukkit.entity.Player;

public class XpUtil {
    public static int getTotalExperience(Player player) {
        int level = player.getLevel();
        int exp = Math.round(getExpAtLevel(level) * player.getExp());
        while (level > 0) {
            level--;
            exp += getExpAtLevel(level);
        }
        return exp;
    }

    private static int getExpAtLevel(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    public static void setTotalExperience(Player player, int amount) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.giveExp(amount);
    }
}