import java.util.Timer;
import java.util.logging.Logger;

public class Skills extends Plugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static final SkillsListener listener = new SkillsListener();
	private static final Timer timer = new Timer();
	public static final String name = "Skills";
	public static final String version = "0.5";
	
	public void initialize() {
		if(!SkillsProperties.loadConfig()){
			log.warning("Can't load properties!");
			//TODO: Here should be load of default config... But it still balancing, and im too lazy to rewrite default function every time...
			return;
		}
		timer.scheduleAtFixedRate(new SkillsTimer(), 0L, SkillsProperties.saveTimer);
		etc.getLoader().addListener(PluginLoader.Hook.ATTACK, listener, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, listener, this, PluginListener.Priority.HIGH);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.EXPLODE, listener, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, listener, this, PluginListener.Priority.LOW);
		log.info(name + " " + version + " initialized");
	}

	public void enable() {
		etc.getInstance().addCommand("/skills", "- Shows your skills level and experience.");
		etc.getInstance().addCommand("/skillinfo", "- Shows skill informaton.");
		etc.getInstance().addCommand("/giveexp", "- Give experience to player.");
		etc.getInstance().addCommand("/reset", "- Reset all skills to first level.");
		etc.getInstance().addCommand("/setmods", "- Set armors and weapons modifiers.");
		etc.getInstance().addCommand("/debug", "- Turn debug messages on\\off for all players.");
		
	}

	public void disable() {
		etc.getInstance().removeCommand("/skills");
		etc.getInstance().removeCommand("/skillinfo");
		etc.getInstance().removeCommand("/giveexp");
		etc.getInstance().removeCommand("/reset");
		etc.getInstance().removeCommand("/setmods");
		etc.getInstance().removeCommand("/debug");
	}
}
