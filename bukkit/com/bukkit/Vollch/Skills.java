package com.bukkit.Vollch;

import java.io.File;
import java.util.logging.Logger;
import java.util.Timer;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Skills for Bukkit
 *
 * @author Vollch
 */
public class Skills extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static final Timer timer = new Timer();
	
	private final SkillsEntityListener entityListener = new SkillsEntityListener(this);
    private final SkillsPlayerListener playerListener = new SkillsPlayerListener(this);
    private final SkillsBlockListener blockListener = new SkillsBlockListener(this);

    public Skills(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, plugin, cLoader);
    }

    public void onEnable() {
		if(!SkillsProperties.loadConfig()){
			log.warning("An error has occurred during loading properties! Load default.");
			SkillsProperties.defaultConfig();
			if(!SkillsProperties.loadConfig()){
				log.warning("An error has occurred during loading default properties! Stopping.");
				return;
			}
		}
		timer.scheduleAtFixedRate(new SkillsTimer(), 0L, SkillsProperties.saveTimer);
		
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Normal, this);
       
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
    public void onDisable() {
    }
}

