package com.bukkit.Vollch;
import java.util.TimerTask;

public class SkillsTimer extends TimerTask {
	public SkillsTimer() {
	}

	public void run() {
        SkillsPlayer.saveExp();
        SkillsProperties.saveDurability();
	}
}
