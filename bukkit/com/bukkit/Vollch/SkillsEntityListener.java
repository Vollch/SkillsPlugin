package com.bukkit.Vollch;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class SkillsEntityListener extends EntityListener {
	private static final Logger log = Logger.getLogger("Minecraft");
	//private Skills plugin;

    public SkillsEntityListener(final Skills plugin) {
    	//this.plugin = plugin;
    }
    
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
    	if(event.getEntity() instanceof Player)
    		log.info("Block");
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	if(event.getEntity() instanceof Player)
    		log.info("Entity");
    }
    
    public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
    	if(event.getEntity() instanceof Player)
    		log.info("Projectile");
    }
    
    public void onEntityCombust(EntityCombustEvent event) {
    	if(event.getEntity() instanceof Player)
    		log.info("Combust");
    }

    public void onEntityDamage(EntityDamageEvent event) {
    	if(event.getEntity() instanceof Player)
    		log.info("Damage");
    }
}