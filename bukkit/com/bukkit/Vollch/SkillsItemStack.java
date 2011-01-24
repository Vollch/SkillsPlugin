package com.bukkit.Vollch;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;


public class SkillsItemStack {
	net.minecraft.server.ItemStack item;
	public SkillsItemStack(ItemStack it){
		try{
	    	CraftItemStack ci = (CraftItemStack)it;
			Class<? extends CraftItemStack> c = ci.getClass();
			Field f = c.getDeclaredField("item");
			f.setAccessible(true);
			this.item = (net.minecraft.server.ItemStack)f.get(ci);
		} 
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
    public void setDamage(int damage){
		this.item.d = damage;
    }

    public int getDamage(){
		return this.item.d;
    }
}
