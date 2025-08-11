package me.samsuik.cannonessentials.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.samsuik.cannonessentials.CannonEssentials;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public final class TntFillCommand implements BasicCommand {
    private final CannonEssentials plugin;

    public TntFillCommand(final CannonEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSourceStack stack, final String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) {
            return;
        }

        final Location location = player.getLocation();
        final int fillRadius = plugin.getConfig().getInt("tnt-fill.radius", 64);
        int totalFilledTnt = 0;

        final int minChunkX = (location.getBlockX() - fillRadius) >> 4;
        final int minChunkZ = (location.getBlockZ() - fillRadius) >> 4;
        final int maxChunkX = (location.getBlockX() + fillRadius) >> 4;
        final int maxChunkZ = (location.getBlockZ() + fillRadius) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                final Chunk chunk = location.getWorld().getChunkAt(chunkX, chunkZ, false);
                if (!chunk.isLoaded()) {
                    continue;
                }

                for (final BlockState state : chunk.getTileEntities()) {
                    if (state instanceof Dispenser dispenser && dispenser.getLocation().distance(location) < fillRadius) {
                        final Inventory inventory = dispenser.getInventory();
                        final int itemsToAdd = inventory.getSize() * 64;
                        final ItemStack tnt = new ItemStack(Material.TNT, itemsToAdd);
                        final Map<Integer, ItemStack> remainingItems = dispenser.getInventory().addItem(tnt);

                        // Keep track of how much tnt we added
                        for (final ItemStack remaining : remainingItems.values()) {
                            totalFilledTnt -= remaining.getAmount();
                        }

                        totalFilledTnt += itemsToAdd;
                    }
                }
            }
        }

        player.sendRichMessage(
                plugin.getConfig().getString("tnt-fill.message", ""),
                Placeholder.unparsed("count", String.valueOf(totalFilledTnt))
        );
    }

    @Override
    public String permission() {
        return "me.samsuik.cannonessentials.tntfill";
    }
}
