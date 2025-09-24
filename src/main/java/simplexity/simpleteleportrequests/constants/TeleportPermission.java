package simplexity.simpleteleportrequests.constants;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public enum TeleportPermission {
    BASIC_TELEPORT(new Permission("teleport.basic", "Allows basic teleport request functionality", PermissionDefault.TRUE));
    private final Permission permission;

    TeleportPermission(Permission permission) {
        this.permission = permission;
    }

    @NotNull
    public Permission getPermission() {
        return permission;
    }
}
