package me.samsuik.cannonessentials.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.samsuik.cannonessentials.CannonEssentials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

@NullMarked
public final class TntProtection implements Listener {
    private final Map<ProtectedLocation, Boolean> protection = new HashMap<>();
    private final Set<Material> protectionBlocks = new HashSet<>();

    public TntProtection(final CannonEssentials plugin) {
        for (final String protectionMaterial : plugin.getConfig().getStringList("protection-materials")) {
            final Material material = Material.matchMaterial(protectionMaterial);
            if (material != null) {
                this.protectionBlocks.add(material);
            }
        }
    }

    @EventHandler
    public void tickEvent(final ServerTickStartEvent tickEvent) {
        if (tickEvent.getTickNumber() % 20 == 0) {
            this.protection.clear();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void entityExplode(final EntityExplodeEvent explodeEvent) {
        if (explodeEvent.isCancelled() || this.isProtected(explodeEvent.getLocation())) {
            explodeEvent.blockList().clear();
            return;
        }

        for (final Block block : explodeEvent.blockList()) {
            if (this.isProtected(block.getLocation())) {
                explodeEvent.blockList().clear();
                return;
            }
        }
    }

    private boolean isProtected(final Location location) {
        final World world = location.getWorld();
        final ProtectedLocation protectedLocation = ProtectedLocation.fromLocation(location);
        final Boolean tntProtection = this.protection.get(protectedLocation);

        if (tntProtection != null) {
            return tntProtection;
        }

        // Check if there are any protection blocks below or above the location
        final boolean hasProtectionBlock = IntStream.range(world.getMinHeight() + 1, world.getMaxHeight())
                .mapToObj(y -> world.getBlockAt(location.blockX(), y, location.getBlockZ()))
                .anyMatch(block -> this.protectionBlocks.contains(block.getType()));

        this.protection.put(protectedLocation, hasProtectionBlock);
        return hasProtectionBlock;
    }

    private record ProtectedLocation(World world, int x, int z) {
        public static ProtectedLocation fromLocation(final Location location) {
            return new ProtectedLocation(location.getWorld(), location.blockX(), location.blockZ());
        }
    }
}
