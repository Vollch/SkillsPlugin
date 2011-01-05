import java.util.logging.Logger;

public class Skills extends Plugin {
	static final SkillsListener listener = new SkillsListener();
	static final Logger log = Logger.getLogger("Minecraft");
	String name = "Skills";
	String version = "0.4";

	public void initialize() {
		log.info(name + " " + version + " initialized");
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ATTACK, listener, this, PluginListener.Priority.MEDIUM);
	}

	public void enable() {
		etc.getInstance().addCommand("/skills", "- Shows your skills level and experience.");
		etc.getInstance().addCommand("/giveexp", "- Give experience to player.");
		etc.getInstance().addCommand("/reset", "- Reset all skills to first level.");
		etc.getInstance().addCommand("/setmods", "- Set armors and weapons modifiers.");
		etc.getInstance().addCommand("/debug", "- Turn debug messages on\\off for all players.");
	}

	public void disable() {
		etc.getInstance().removeCommand("/skills");
		etc.getInstance().removeCommand("/giveexp");
		etc.getInstance().removeCommand("/reset");
		etc.getInstance().removeCommand("/setmods");
		etc.getInstance().removeCommand("/debug");
	}
}
