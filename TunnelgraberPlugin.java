package de.tunnelgraber;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class TunnelgraberListener implements Listener {

    private final TunnelgraberPlugin plugin;

    // Alle Blöcke die mit einer Spitzhacke abbaubar sind
    private static final Set<Material> PICKAXE_MINEABLE = new HashSet<>();

    static {
        // Stein & Varianten
        PICKAXE_MINEABLE.add(Material.STONE);
        PICKAXE_MINEABLE.add(Material.COBBLESTONE);
        PICKAXE_MINEABLE.add(Material.GRANITE);
        PICKAXE_MINEABLE.add(Material.POLISHED_GRANITE);
        PICKAXE_MINEABLE.add(Material.DIORITE);
        PICKAXE_MINEABLE.add(Material.POLISHED_DIORITE);
        PICKAXE_MINEABLE.add(Material.ANDESITE);
        PICKAXE_MINEABLE.add(Material.POLISHED_ANDESITE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE);
        PICKAXE_MINEABLE.add(Material.COBBLED_DEEPSLATE);
        PICKAXE_MINEABLE.add(Material.TUFF);
        PICKAXE_MINEABLE.add(Material.CALCITE);
        PICKAXE_MINEABLE.add(Material.SMOOTH_BASALT);
        PICKAXE_MINEABLE.add(Material.BASALT);
        PICKAXE_MINEABLE.add(Material.BLACKSTONE);
        PICKAXE_MINEABLE.add(Material.NETHERRACK);
        PICKAXE_MINEABLE.add(Material.END_STONE);
        PICKAXE_MINEABLE.add(Material.OBSIDIAN);
        PICKAXE_MINEABLE.add(Material.CRYING_OBSIDIAN);

        // Erze
        PICKAXE_MINEABLE.add(Material.COAL_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_COAL_ORE);
        PICKAXE_MINEABLE.add(Material.IRON_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_IRON_ORE);
        PICKAXE_MINEABLE.add(Material.COPPER_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_COPPER_ORE);
        PICKAXE_MINEABLE.add(Material.GOLD_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_GOLD_ORE);
        PICKAXE_MINEABLE.add(Material.REDSTONE_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_REDSTONE_ORE);
        PICKAXE_MINEABLE.add(Material.LAPIS_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_LAPIS_ORE);
        PICKAXE_MINEABLE.add(Material.DIAMOND_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_DIAMOND_ORE);
        PICKAXE_MINEABLE.add(Material.EMERALD_ORE);
        PICKAXE_MINEABLE.add(Material.DEEPSLATE_EMERALD_ORE);
        PICKAXE_MINEABLE.add(Material.NETHER_GOLD_ORE);
        PICKAXE_MINEABLE.add(Material.NETHER_QUARTZ_ORE);
        PICKAXE_MINEABLE.add(Material.ANCIENT_DEBRIS);

        // Weitere
        PICKAXE_MINEABLE.add(Material.SANDSTONE);
        PICKAXE_MINEABLE.add(Material.RED_SANDSTONE);
        PICKAXE_MINEABLE.add(Material.TERRACOTTA);
        PICKAXE_MINEABLE.add(Material.ICE);
        PICKAXE_MINEABLE.add(Material.PACKED_ICE);
        PICKAXE_MINEABLE.add(Material.BLUE_ICE);
        PICKAXE_MINEABLE.add(Material.PRISMARINE);
        PICKAXE_MINEABLE.add(Material.PRISMARINE_BRICKS);
        PICKAXE_MINEABLE.add(Material.DARK_PRISMARINE);
        PICKAXE_MINEABLE.add(Material.SEA_LANTERN);
        PICKAXE_MINEABLE.add(Material.MAGMA_BLOCK);
        PICKAXE_MINEABLE.add(Material.STONE_BRICKS);
        PICKAXE_MINEABLE.add(Material.MOSSY_STONE_BRICKS);
        PICKAXE_MINEABLE.add(Material.CRACKED_STONE_BRICKS);
        PICKAXE_MINEABLE.add(Material.CHISELED_STONE_BRICKS);
        PICKAXE_MINEABLE.add(Material.INFESTED_STONE);
        PICKAXE_MINEABLE.add(Material.GRAVEL);
        PICKAXE_MINEABLE.add(Material.RAW_IRON_BLOCK);
        PICKAXE_MINEABLE.add(Material.RAW_GOLD_BLOCK);
        PICKAXE_MINEABLE.add(Material.RAW_COPPER_BLOCK);
    }

    public TunnelgraberListener(TunnelgraberPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Prüfe ob Tunnelgräber
        if (!TunnelgraberItem.isTunnelgraber(item)) return;

        // Prüfe ob der Block ein Spitzhacken-Block ist
        Block brokenBlock = event.getBlock();
        if (!PICKAXE_MINEABLE.contains(brokenBlock.getType())) return;

        // 5x5 Mining ausführen (verzögert, damit der Haupt-Block zuerst abbricht)
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            mine5x5(player, brokenBlock);
        });
    }

    private void mine5x5(Player player, Block center) {
        // Richtung des Spielers ermitteln (wohin er schaut)
        org.bukkit.block.BlockFace face = getPlayerFacingFace(player);

        // Achsen bestimmen je nach Blickrichtung
        int[][] offsets = get5x5Offsets(face);

        for (int[] offset : offsets) {
            Block target = center.getRelative(offset[0], offset[1], offset[2]);

            // Nur Spitzhacken-Blöcke abbauen
            if (!PICKAXE_MINEABLE.contains(target.getType())) continue;

            // Block nicht erneut abbauen wenn es der Center-Block ist
            if (target.equals(center)) continue;

            // Block mit Drops abbauen (wie normales Mining)
            target.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    private org.bukkit.block.BlockFace getPlayerFacingFace(Player player) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        // Schaut der Spieler stark nach oben/unten?
        if (pitch < -45) return org.bukkit.block.BlockFace.UP;
        if (pitch > 45) return org.bukkit.block.BlockFace.DOWN;

        // Horizontale Richtung
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw >= 315 || yaw < 45) return org.bukkit.block.BlockFace.NORTH;
        if (yaw >= 45 && yaw < 135) return org.bukkit.block.BlockFace.EAST;
        if (yaw >= 135 && yaw < 225) return org.bukkit.block.BlockFace.SOUTH;
        return org.bukkit.block.BlockFace.WEST;
    }

    private int[][] get5x5Offsets(org.bukkit.block.BlockFace face) {
        // Gibt alle 25 Positionen des 5x5 Rasters zurück (außer Center)
        // Relativ zum abgebauten Block, senkrecht zur Blickrichtung
        int[][] offsets = new int[24][3]; // 25 - 1 (center)
        int idx = 0;

        for (int a = -2; a <= 2; a++) {
            for (int b = -2; b <= 2; b++) {
                if (a == 0 && b == 0) continue; // Center überspringen

                int x = 0, y = 0, z = 0;

                switch (face) {
                    case NORTH:
                    case SOUTH:
                        x = a;
                        y = b;
                        z = 0;
                        break;
                    case EAST:
                    case WEST:
                        x = 0;
                        y = b;
                        z = a;
                        break;
                    case UP:
                    case DOWN:
                        x = a;
                        y = 0;
                        z = b;
                        break;
                    default:
                        x = a;
                        y = b;
                        z = 0;
                }

                offsets[idx++] = new int[]{x, y, z};
            }
        }
        return offsets;
    }
}
