package simplexity.simpleteleportrequests.commands;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import simplexity.simpleteleportrequests.SimpleTeleportRequests;
import simplexity.simpleteleportrequests.config.LocaleMessage;

@SuppressWarnings("UnstableApiUsage")
public class Exceptions {

    private static final MiniMessage miniMessage = SimpleTeleportRequests.getMiniMessage();

    public static final SimpleCommandExceptionType NO_PENDING_REQUESTS = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            LocaleMessage.NO_PENDING_REQUESTS.getMessage()
                    )
            )
    );

    public static final SimpleCommandExceptionType MUST_BE_PLAYER = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            LocaleMessage.MUST_BE_PLAYER.getMessage()
                    )
            )
    );

    public static final SimpleCommandExceptionType PLAYER_LOGGED_OFF = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            LocaleMessage.PLAYER_LOGGED_OFF.getMessage()
                    )
            )
    );

    public static final SimpleCommandExceptionType PLAYER_NOT_FOUND = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            LocaleMessage.PLAYER_DOES_NOT_EXIST.getMessage()
                    )
            )
    );

    public static final SimpleCommandExceptionType NO_PENDING_REQUESTS_BY_THAT_NAME = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            LocaleMessage.NO_REQUESTS_BY_THAT_NAME.getMessage()
                    )
            )
    );

    public static final SimpleCommandExceptionType CANNOT_TELEPORT_TO_SELF = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            LocaleMessage.TELEPORT_REQUEST_SELF.getMessage()
                    )
            )
    );
}
