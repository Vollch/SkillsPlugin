package com.bukkit.Vollch;

import org.bukkit.BlockDamageLevel;
import org.bukkit.event.block.BlockDamagedEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlacedEvent;

public class SkillsBlockListener extends BlockListener {
	//private Skills plugin;

    public SkillsBlockListener(final Skills plugin) {
    	//this.plugin = plugin;
    }

    public void onBlockDamaged(BlockDamagedEvent event) {
        if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
        	SkillsPlayer sp = SkillsPlayer.get(event.getPlayer());
        	
    		int skill = SkillsProperties.getDestroySkill(event.getBlock().getTypeID());
    		if(skill < 1)
    			return;
    		int tool = SkillsProperties.getItemLevel(event.getPlayer().getItemInHand().getTypeID(), skill);
    		int durability = SkillsProperties.getBlockDurability(event.getBlock());
    		if(SkillsProperties.debugOn){
    			event.getPlayer().sendMessage("Skill - " + Integer.toString(sp.getLevel(skill)) + " Tool - " + Integer.toString(tool) + " Dura - " + Integer.toString(durability));
    		}
    		if(durability - (sp.getLevel(skill) + tool) > SkillsProperties.toBroke) {
    			event.getPlayer().sendMessage("This block is too hard for you!");
    		}
    		else {
    			if(sp.getLevel(skill) + tool >= durability) {
    				SkillsProperties.setBlockDurability(event.getBlock(), 0);
    				if(SkillsProperties.levelDependentDestroyGain){
    					sp.giveExp(skill, durability);
    				}
    				else{
    					sp.giveExp(skill, 1);
    				}
    				return;
    			}
    			else {
    				durability -= 1;
    				SkillsProperties.setBlockDurability(event.getBlock(), durability);
    				if(SkillsProperties.levelDependentDestroyGain){
    					sp.giveExp(skill, 1);
    				}
    				event.setCancelled(true);
    				return;
    			}
    		}
    		event.setCancelled(true);
    		return;
        }
    }
    
    public void onBlockPlaced(BlockPlacedEvent event) {
		int skill = SkillsProperties.getCreateSkill(event.getBlockPlaced().getTypeID());
		if(skill < 1){
			return;
		}
		SkillsPlayer sp = SkillsPlayer.get(event.getPlayer());
		
		if(SkillsProperties.debugOn){
			event.getPlayer().sendMessage("Skill - " + sp.getLevel(skill) + "Block - " + event.getBlockPlaced().getTypeID());
		}
		
		SkillsProperties.setBlockDurability(event.getBlock(), sp.getLevel(skill));

		if(SkillsProperties.levelDependentCreatyGain){
			sp.giveExp(skill, sp.getLevel(skill));
		}
		else{
			sp.giveExp(skill, 1);
		}
		return;
    }
}
