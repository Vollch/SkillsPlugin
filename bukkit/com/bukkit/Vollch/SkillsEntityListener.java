package com.bukkit.Vollch;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.server.EntityLiving;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SkillsEntityListener extends EntityListener {
	// private Skills plugin;
	public SkillsEntityListener(final Skills plugin) {
		// this.plugin = plugin;
	}

	public void doDamage(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
		Player aplayer = null;
		Player dplayer = null;
		if(event.getEntity() instanceof Player) {
			dplayer = (Player) event.getEntity();
		}
		if(event.getDamager() instanceof Player) {
			aplayer = (Player) event.getDamager();
		}
		double hit = 0;
		double def = 0;
		int wskill = 0;
		if(aplayer != null) {
			int wdamage = 0;
			if(event instanceof EntityDamageByProjectileEvent) {
				wskill = SkillsProperties.getProjectileSkill(aplayer.getItemInHand().getTypeId());
			}
			else {
				wskill = SkillsProperties.getWeaponSkill(aplayer.getItemInHand().getTypeId());
			}
			if(wskill < 1) {
				wskill = SkillsProperties.getWeaponSkill(399);
				wdamage = SkillsProperties.getItemLevel(399, wskill);
			}
			else {
				wdamage = SkillsProperties.getItemLevel(aplayer.getItemInHand().getTypeId(), wskill);
			}
			int wlevel = SkillsPlayer.get(aplayer).getLevel(wskill);
			if(dplayer != null) {
				hit = wdamage * (1 + (wlevel * SkillsProperties.weaponMod));
			}
			else {
				hit = wdamage * (1 + (wlevel * (SkillsProperties.weaponMod / 3)));
			}
		}
		else {
			hit = event.getDamage() * SkillsProperties.monsterMod;
		}
		if(dplayer != null) {
			if(SkillsProperties.Dodge != null){
				double dodge = SkillsProperties.Dodge[SkillsPlayer.get(dplayer).getLevel((int) SkillsProperties.Dodge[0])];
				if(dodge >= Math.random()) {
					SkillsPlayer.get(dplayer).giveExp((int) SkillsProperties.Dodge[0], 1);
					dplayer.sendMessage("You dodge enemy attack!");
					if(aplayer != null) {
						aplayer.sendMessage("Enemy dodged!");
					}
					return;
				}
			}
			Inventory inv = dplayer.getInventory();
			for(int i = 36; i < 40; i++) {
				int id = inv.getItem(i).getTypeId();
				int askill = SkillsProperties.getArmorSkill(id);
				if(askill > 0) {
					SkillsPlayer.get(dplayer).giveExp(askill, 1);
					def += SkillsProperties.getItemLevel(id, askill);
					int ahit = (int) hit - SkillsPlayer.get(dplayer).getLevel(askill);
					if(ahit > 0) {
						if(inv.getItem(i).getDurability() + ahit < SkillsProperties.getArmorDurability(id)) {
							inv.getItem(i).setDurability((short) (inv.getItem(i).getDurability() + ahit));
						}
						else {
							inv.clear(i);
						}
					}
				}
			}
			if(aplayer != null) {
				def *= SkillsProperties.armorMod;
			}
			else {
				def *= SkillsProperties.armorMod / 3;
			}
		}
		else {
			def = 0;
		}
		if(SkillsProperties.debugOn && aplayer != null) {
			aplayer.sendMessage("Hit: " + (double) hit + " Def: " + (double) def + " To:" + event.getEntity().toString());
		}
		if(SkillsProperties.debugOn && dplayer != null) {
			dplayer.sendMessage("Def: " + (double) def + " Hit: " + (double) hit + " From:" + event.getDamager().toString());
		}
		int dmg = (int)hit - (int)def;
		if(dmg > 0) {
			EntityLiving df = (EntityLiving)((CraftEntity)event.getEntity()).getHandle();
			EntityLiving at = (EntityLiving)((CraftEntity)event.getDamager()).getHandle();
			df.a(at, dmg);
			
			if(aplayer != null && (SkillsProperties.creatureCombatGain || dplayer != null)) {
				SkillsPlayer.get(aplayer).giveExp(wskill, dmg);
			}
			if(df.health < 1){
				if(dplayer != null){
					dplayer.getInventory().clear();
				}
				if(aplayer != null) {
					SkillsPlayer sp = SkillsPlayer.get(aplayer);
					String foe = event.getEntity().getClass().getName().substring(35);
					int dropSkill = SkillsProperties.getDropSkill(foe);
					if(dropSkill > 0) {
						sp.giveExp(dropSkill, 1);
						Integer[] items = SkillsProperties.getDrop(foe, sp.getLevel(dropSkill));
						if(items.length > 0) {
							if(SkillsProperties.levelDependentGatherGain) {
								sp.giveExp(dropSkill, items.length);
							}
							for(int i = 0; i < items.length; i++) {
								event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(items[i], 1));
							}
						}
					}
				}
			}
		}
	}
	
	public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
		if(SkillsProperties.combatOn && !event.isCancelled() && event.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity){
			doDamage(event);
		}
		return;
	}

	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(SkillsProperties.combatOn && !event.isCancelled() && event.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity){
			doDamage(event);
		}
		return;
	}

	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.isCancelled())
			return;
		event.setCancelled(true);
		Random rand = new Random();
		Location loc = event.getLocation();
		Vector center = new Vector(loc.getX(), loc.getY(), loc.getZ());
		List<Block> blocks = event.blockList();
		Iterator<Block> itr = blocks.iterator();
		while(itr.hasNext()) {
			Block block = itr.next();
			if(block.getTypeId() == 51 || block.getTypeId() == 7 || block.getTypeId() == 0) {
				continue;
			}
			else if(block.getTypeId() == 46) {
				Block up = block.getRelative(BlockFace.UP);
				if(up.getTypeId() == 0) {
					up.setTypeId(51);
				}
			}
			else {
				Vector other = new Vector(block.getX(), block.getY(), block.getZ());
				int durability = SkillsProperties.getBlockDurability(block) - (6 - (int) center.distance(other));
				if(durability < 1) {
					if(net.minecraft.server.Block.byId[block.getTypeId()].a(rand) > 0) {
						if(SkillsProperties.explosionDrop >= Math.random()) {
							ItemStack drop = new ItemStack(net.minecraft.server.Block.byId[block.getTypeId()].a(0, rand), net.minecraft.server.Block.byId[block.getTypeId()].a(rand));
							block.getWorld().dropItem(block.getLocation(), drop);
						}
					}
					block.setTypeId(0);
				}
				else {
					SkillsProperties.setBlockDurability(block, durability);
				}
			}
		}
	}
}
