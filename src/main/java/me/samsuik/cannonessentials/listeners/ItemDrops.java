package me.samsuik.cannonessentials.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.samsuik.cannonessentials.CannonEssentials;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
