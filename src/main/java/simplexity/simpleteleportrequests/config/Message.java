package simplexity.simpleteleportrequests.config;

public enum Message {
    TELEPORT_ASK_SENT("teleport.ask.sent", "<bold><gold>You</gold></bold> <green>have requested to teleport to <yellow><player></yellow>. They have <yellow><value></yellow> seconds to respond</green>"),
    TELEPORT_ASK_RECEIVED("teleport.ask.received", "<yellow><player></yellow><green> has requested to teleport to <bold><gold>You</gold></bold>. You have <yellow><value></yellow> seconds to respond</green>"),
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
