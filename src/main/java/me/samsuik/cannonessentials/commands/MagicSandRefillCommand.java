package me.samsuik.cannonessentials.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.samsuik.cannonessentials.CannonEssentials;
import me.samsuik.cannonessentials.listeners.MagicSand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MagicSandRefillCommand implements BasicCommand {
    private final CannonEssentials plugin;
    private final MagicSand magicSand;

    public MagicSandRefillCommand(final CannonEssentials plugin, final MagicSand magicSand) {
        this.plugin = plugin;
        this.magicSand = magicSand;
    }

    @Override
    public void execute(final CommandSourceStack stack, final String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) {
            return;
        }

        final Location location = player.getLocation();
        final int msRadius = this.plugin.getConfig().getInt("magic-sand.radius", 32);
        int magicSand = 0;

        search: for (int x = -msRadius; x <= msRadius; ++x) {
            for (int y = -msRadius; y <= msRadius; ++y) {
                for (int z = -msRadius; z <= msRadius; ++z) {
                    final Block block = location.getBlock().getRelative(x, y, z);
                    if (block.getType() == Material.CLAY) {
                        for (int distance = 1; distance < 48; ++distance) {
                            final Block belowBlock = block.getRelative(BlockFace.DOWN, distance);
                            if (!belowBlock.getType().hasGravity()) {
                                continue;
                            }

                            if (this.magicSand.placeMagicSand(player, block, belowBlock.getType())) {
                                break; // reactivated magicsand
                            } else {
                                break search; // hit the magicsand limit
                            }
                        }
                    }
                }
            }
        }

        player.sendRichMessage(
                plugin.getConfig().getString("magic-sand.refill", ""),
                Placeholder.unparsed("count", String.valueOf(magicSand))
        );
    }
}
