package rexagon.rexaeco.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RexaMenu implements InventoryHolder {
    private Inventory inventory;
    private final Map<Integer, List<String>> leftClickCommands = new HashMap<>();
    private final Map<Integer, List<String>> rightClickCommands = new HashMap<>();

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void addActions(int slot, List<String> leftCmds, List<String> rightCmds) {
        if (leftCmds != null) leftClickCommands.put(slot, leftCmds);
        if (rightCmds != null) rightClickCommands.put(slot, rightCmds);
    }

    public List<String> getLeftClickCommands(int slot) { return leftClickCommands.get(slot); }
    public List<String> getRightClickCommands(int slot) { return rightClickCommands.get(slot); }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}