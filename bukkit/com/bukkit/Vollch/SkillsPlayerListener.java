package com.bukkit.Vollch;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class SkillsPlayerListener extends PlayerListener {
	private Skills plugin;

	public SkillsPlayerListener(final Skills plugin) {
		this.plugin = plugin;
	}

    public void onPlayerCommand(PlayerChatEvent event) {
    	String[] split = event.getMessage().split(" ");
    	Player player = event.getPlayer();
    	
		if(split[0].equalsIgnoreCase("/skillsversion") || split[0].equalsIgnoreCase("/skillsversion")) {	
			event.setCancelled(true);
			player.sendMessage(plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
		}
		else if(split[0].equalsIgnoreCase("/skillinfo")) {	
			event.setCancelled(true);
			if(split.length > 1){
				int skill = 0;
				for(int i = 1; i < SkillsProperties.Skills.length; i++) {
					if(SkillsProperties.Skills[i].regionMatches(true, 0, split[1], 0, split[1].length())) {
						skill = i;
						break;
					}
				}
				if(skill > 0){
					player.sendMessage(SkillsProperties.getSkillName(skill) + " is skill for:");
					String sDestroy = "Gather: ";
					boolean bDestroy = false;
					for(int i = 1; i < 100; i++){
						if(SkillsProperties.getDestroySkill(i) == skill){
							if(!bDestroy){
								bDestroy = true;
								sDestroy += Material.getMaterial(i);
							}
							else{
								sDestroy += ", "+Material.getMaterial(i);
							}
							
						}
					}
					if(bDestroy){
						player.sendMessage(sDestroy);
					}
					String sCreate = "Build: ";
					boolean bCreate = false;
					for(int i = 1; i < 100; i++){
						if(SkillsProperties.getCreateSkill(i) == skill){
							if(!bCreate){
								bCreate = true;
								sCreate += Material.getMaterial(i);
							}
							else{
								sCreate += ", "+Material.getMaterial(i);
							}
							
						}
					}
					if(bCreate){
						player.sendMessage(sCreate);
					}
					return;
				}
			}
			player.sendMessage("Correct usage is: /skillinfo [skillname]");
		}
		else if(split[0].equalsIgnoreCase("/skills")) {	
			event.setCancelled(true);
			Player p;
			if(split.length > 1){
				p = plugin.getServer().getPlayer(split[1]);
			}
			else{
				p = player;
			}
			if(p != null){
				SkillsPlayer sp = SkillsPlayer.get(p);
				player.sendMessage(sp.getName() + " are:");
				for(int skill = 1; skill < SkillsProperties.Skills.length; skill++) {
					int level = sp.getLevel(skill);
					if(level < 15){
						player.sendMessage(SkillsProperties.getRangForLevel(level, skill) + " " + SkillsProperties.Skills[skill] + "; level - " + level + "; exp - " + sp.getExp(skill) + "(" + SkillsProperties.getExpForLevel(level + 1, skill) + ");");
					}
					else{
						player.sendMessage(SkillsProperties.getRangForLevel(level, skill) + " " + SkillsProperties.Skills[skill] + "; level - " + level + "; exp - " + sp.getExp(skill) + ";");
					}
				}
				return;
			}
			player.sendMessage("Correct usage is: /skills <playername>");
		}
//		else if(split[0].equalsIgnoreCase("/reset")) {
//			event.setCancelled(true);
//			Player p;
//			if(split.length > 1){
//				p = plugin.getServer().getPlayer(split[1]);
//			}
//			else{
//				p = player;
//			}
//			if(p != null){
//				SkillsPlayer sp = SkillsPlayer.get(p);
//				for(int i = 1; i < SkillsProperties.Skills.length; i++) {
//					sp.setExp(i, 0);
//					sp.setLevel(i, 1);
//				}
//				player.sendMessage("Done!");
//				return;
//			}
//			player.sendMessage("Correct usage is: /reset <playername>");
//		}
//		else if(split[0].equalsIgnoreCase("/giveexp")) {
//			event.setCancelled(true);
//			if(split.length > 3){
//				Player p = plugin.getServer().getPlayer(split[1]);
//				if(p != null){
//					SkillsPlayer sp = SkillsPlayer.get(p);
//					int skill = 0;
//					int amount = Integer.parseInt(split[3]);
//					for(int i = 1; i < SkillsProperties.Skills.length; i++) {
//						if(SkillsProperties.Skills[i].regionMatches(true, 0, split[2], 0, split[2].length())) {
//							skill = i;
//							break;
//						}
//					}
//					if(amount != 0 && skill > 0 && sp != null) {
//						sp.giveExp(skill, amount);
//						player.sendMessage("Done!");
//						return;
//					}
//				}
//			}
//			player.sendMessage("Correct usage is: /giveexp [playername] [skillname] [amount]");
//		}
		else if(split[0].equalsIgnoreCase("/debug")) {
			event.setCancelled(true);			
			if(SkillsProperties.debugOn){
				SkillsProperties.debugOn = false;
				player.sendMessage("Debug messages is now not showing.");
			}
			else{
				SkillsProperties.debugOn = true;
				player.sendMessage("Debug messages is now showing.");
			}
		}
		return;
    }
}