package com.bukkit.Vollch;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class SkillsBlockListener extends BlockListener {
	//private Skills plugin;

    public SkillsBlockListener(final Skills plugin) {
    	//this.plugin = plugin;
    }

    public void onBlockDamaged(BlockDamageEvent event) {
        if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
        	SkillsPlayer sp = SkillsPlayer.get(event.getPlayer());
        	
    		int skillDestroy = SkillsProperties.getDestroySkill(event.getBlock().getTypeId());
    		if(skillDestroy > 0){
	    		int tool = SkillsProperties.getItemLevel(event.getPlayer().getItemInHand().getTypeId(), skillDestroy);
	    		int durability = SkillsProperties.getBlockDurability(event.getBlock());
	    		if(SkillsProperties.debugOn){
	    			event.getPlayer().sendMessage("Skill - " + Integer.toString(sp.getLevel(skillDestroy)) + " Tool - " + Integer.toString(tool) + " Dura - " + Integer.toString(durability));
	    		}
	    		if(durability - (sp.getLevel(skillDestroy) + tool) > SkillsProperties.toBroke) {
	    			event.getPlayer().sendMessage("This block is too hard for you!");
	    			event.setCancelled(true);
	    			return;
	    		}
	    		else {
	    			if(sp.getLevel(skillDestroy) + tool >= durability) {
	    				SkillsProperties.setBlockDurability(event.getBlock(), 0);
	    				if(SkillsProperties.levelDependentDestroyGain){
	    					sp.giveExp(skillDestroy, durability);
	    				}
	    				else{
	    					sp.giveExp(skillDestroy, 1);
	    				}
	    			}
	    			else {
	    				durability -= 1;
	    				SkillsProperties.setBlockDurability(event.getBlock(), durability);
	    				if(SkillsProperties.levelDependentDestroyGain){
	    					sp.giveExp(skillDestroy, 1);
	    				}
	    				event.setCancelled(true);
	    				return;
	    			}
	    		}
    		}
    		int skillGather = SkillsProperties.getGatherSkill(event.getBlock().getTypeId());
    		if(skillGather > 0){
    			sp.giveExp(skillGather, 1);
    			Integer[] items = SkillsProperties.getDrop(event.getBlock(), sp.getLevel(skillGather));
    			
    			if(items.length > 0){
    				if(SkillsProperties.levelDependentGatherGain){
    					sp.giveExp(skillGather, items.length);
    				}
    				World world = event.getPlayer().getWorld();
    				Location loc = new Location(world, event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
    				for(int i = 0; i < items.length; i++){
    					world.dropItem(loc, new ItemStack(items[i], 1));
    				}
    			}
    		}
        }
    }
    
   
    public void onBlockPlaced(BlockPlaceEvent event) {
		int skill = SkillsProperties.getCreateSkill(event.getBlockPlaced().getTypeId());
		if(skill < 1){
			return;
		}
		SkillsPlayer sp = SkillsPlayer.get(event.getPlayer());
		
		if(SkillsProperties.debugOn){
			event.getPlayer().sendMessage("Skill - " + sp.getLevel(skill) + "Block - " + event.getBlockPlaced().getTypeId());
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
