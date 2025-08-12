package me.samsuik.cannonessentials.listeners;

import me.samsuik.cannonessentials.CannonEssentials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ItemDrops implements Listener {
    private final CannonEssentials plugin;

    public ItemDrops(final CannonEssentials plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void disableDispenserDrops(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Location location = block.getLocation();

        if (event.isDropItems() && block.getType() == Material.DISPENSER) {
            event.setDropItems(!plugin.getConfig().getBoolean("disable-dispenser-drops", false));
        }
    }
}
