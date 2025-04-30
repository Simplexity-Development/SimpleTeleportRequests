package simplexity.simpleteleportrequests.config;

public enum Message {
    TELEPORT_ASK_SENT("teleport.ask.sent", "<bold><gold>You</gold></bold> <green>have requested to teleport to <yellow><player></yellow>. They have <yellow><value></yellow> seconds to respond</green>"),
    TELEPORT_ASK_RECEIVED("teleport.ask.received", "<yellow><player></yellow><green> has requested to teleport to <bold><gold>You</gold></bold>. You have <yellow><value></yellow> seconds to respond</green>"),
    REQUEST_TIMED_OUT_TELEPORTING_PLAYER("teleport.request.timed-out.teleporting-player", "<gray>The pending teleport request to teleport to <player> has timed out</gray>"),
    REQUEST_TIMED_OUT_DESTINATION_PLAYER("teleport.request.timed-out.destination-player", "<gray>The pending teleport request for <player> to teleport to you has timed out</gray>"),
    NO_PENDING_REQUESTS("teleport.request.no-pending-requests", "<gray>You do not have any pending teleport requests</gray>"),
    TELEPORT_REQUEST_CANCELLED("teleport.request.cancelled", "<gray>Your pending teleport request has been cancelled</gray>"),
    TELEPORT_REQUEST_ALREADY_TO_THAT_PERSON("teleport.request.already-to-that-person", "<red>You have already requested to teleport to this person, please wait for them to respond</red>"),
    TELEPORT_REQUEST_ALREADY_PENDING("teleport.request.already-pending", "<red>You cannot request to teleport to multiple locations at once, please cancel your pending one or wait for it to expire</red>"),
    TELEPORT_REQUEST_SENT_BY_TARGET("teleport.request.sent-by-target", "<red>A teleport request has already been sent to you by <player>, please run /tpaccept to accept it.</red>"),
    TELEPORT_REQUEST_SELF("teleport.request.self", "<green>Why are you trying to teleport to yourself? You're literally already here.</green>"),
    PLAYER_DOES_NOT_EXIST("error.player-does-not-exist", "<red>The player you have provided either does not exist, or is not online. Please check your syntax and try again</red>"),
    MUST_SUPPLY_PLAYER("error.must-supply-player", "<red>You must supply a player</red>"),
    MUST_BE_PLAYER("error.must-be-player", "<red>Sorry, you must be a player to run this command</red>"),

    ;

    private final String path;
    private String message;

    Message(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
