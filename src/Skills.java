import java.util.logging.Logger;

public class Skills extends Plugin {
	static final SkillsListener listener = new SkillsListener();
	static final Logger log = Logger.getLogger("Minecraft");
	String name = "Skills";
	String version = "0.3";

	public void initialize() {
		log.info(name + " " + version + " initialized");
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ATTACK, listener, this, PluginListener.Priority.MEDIUM);
	}

	public void enable() {
		etc.getInstance().addCommand("/exp", "- Shows your skills level and experience.");
		etc.getInstance().addCommand("/giveexp", "- Give experience to player.");
	}

	public void disable() {
		etc.getInstance().removeCommand("/exp");
		etc.getInstance().removeCommand("/giveexp");
	}
}
