package simplexity.simpleteleportrequests.safety;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SafetyCheck {

    public static int checkSafetyFlags(Location location, Player player) {
        int flags = 0;
        Location blockAbove = location.clone().add(0, 1, 0);
        Location blockBelow = location.clone().add(0, -1, 0);
        // Fall check, is the player in the air? i.e. is the block below them empty/air?
        if (isEmpty(blockBelow) || blockBelow.getBlock().isPassable()) {
            if (!player.isFlying()) {
                flags |= SafetyFlags.FALLING.bitFlag;
            }
        }
        // Is there lava?
        if (isMaterial(location, Material.LAVA) || isMaterial(blockAbove, Material.LAVA)) {
            if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                flags |= SafetyFlags.LAVA.bitFlag;
            }
        }
        // Is the home encased in blocks?
        if (blockAbove.getBlock().isSolid()) {
            flags |= SafetyFlags.SUFFOCATION.bitFlag;
        }
        // Is the home underwater?
        if (isMaterial(blockAbove, Material.WATER)) {
            if (!(player.hasPotionEffect(PotionEffectType.CONDUIT_POWER) || player.hasPotionEffect(PotionEffectType.WATER_BREATHING))) {
                flags |= SafetyFlags.UNDERWATER.bitFlag;
            }
        }
        return flags;
    }

    private static boolean isMaterial(Location location, Material material) {
        return location.getBlock().getType() == material;
    }

    private static boolean isEmpty(Location location) {
        return location.getBlock().getType().isEmpty();
    }

}

