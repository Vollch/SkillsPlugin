import java.util.Date;

public class SkillsListener extends PluginListener {
	public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
    	if(defender.isPlayer()){
    		Player dplayer = defender.getPlayer();
    		if(!SkillsPlayer.get(dplayer).getDelay(new Date())){
    			return true;
    		}	
        	double dodge = SkillsProperties.Dodge[SkillsPlayer.get(dplayer).getLevel((int)SkillsProperties.Dodge[0])];
        	if(Math.random() < dodge){
        		SkillsPlayer.get(dplayer).giveExp((int)SkillsProperties.Dodge[0], 1);
        		defender.getPlayer().sendMessage("You dodge enemy attack!");
        		if(attacker.isPlayer()){
        			attacker.getPlayer().sendMessage("Enemy dodged!");
        		}
        		return true;
        	} 
    	}
    	return false;
    }
	
    public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
    	if(!SkillsProperties.combatOn)
    		return false;
    	
    	if(type == PluginLoader.DamageType.ENTITY){
    		Player aplayer = null;
    		Player dplayer = null;
    		double hit = 0;
    		double def = 0;
    		int wskill = 0;
    		
    		if(attacker.isPlayer()){
	    		aplayer = attacker.getPlayer();
	    		wskill = SkillsProperties.getWeaponSkill(aplayer.getItemInHand());
	        	if(wskill == 0){
	        		wskill = SkillsProperties.getWeaponSkill(399);
	        	}
            	int wdamage = SkillsProperties.getItemLevel(aplayer.getItemInHand(), wskill);
            	int wlevel = SkillsPlayer.get(aplayer).getLevel(wskill);
            	hit = wdamage * (1 + (wlevel * SkillsProperties.weaponMod));
    		}
    		else
    		{
    			hit = amount * SkillsProperties.monsterMod;
    		}
        	
    		if(defender.isPlayer()){
    			dplayer = defender.getPlayer();
            	Inventory inv = dplayer.getInventory();
            	def = 0;
            	for(int slot = 36; slot < 40; slot++){
            		Item it = inv.getItemFromSlot(slot);
            		if(it != null){
            			int id = it.getItemId();
            			int askill = SkillsProperties.getArmorSkill(id);
            			if(askill > 0){
            				SkillsPlayer.get(dplayer).giveExp(askill, 1);
                			def += SkillsProperties.getItemLevel(id, askill);
                			int ahit = (int)hit - SkillsPlayer.get(dplayer).getLevel(askill);
                			if(ahit > 0){
                				if(it.getDamage() + ahit < SkillsProperties.getArmorDurability(id)){
                					inv.setSlot(id, 1, it.getDamage() + ahit, slot);
                				}
                				else
                				{
                					inv.removeItem(slot);
                				}
                			}
            			}
            		}
            	}
            	inv.update();
            	def *= SkillsProperties.armorMod;
    		}
    		else
    		{
    			hit /= 2;
    			def = 0;
    		}
    		
        	if(SkillsProperties.debugOn && aplayer != null){
        		aplayer.sendMessage("Hit: "+(double)hit+" Def: "+(double)def);
        	}
        	if(SkillsProperties.debugOn && dplayer != null){
        		dplayer.sendMessage("Def: "+(double)def+" Hit: "+(double)hit);
        	}
        	
    		int dmg = (int)hit - (int)def;       	
        	if(dmg > 0){
        		if(attacker.isPlayer()){
        			SkillsPlayer.get(aplayer).giveExp(wskill, dmg);
        		}
        		int hp = ((LivingEntity)defender).getHealth() - dmg;
        		
            	if(hp < 1){
            		((LivingEntity)defender).setHealth(1);
            		return false;
            	}
            	else
            	{
            		lc dlc = ((LivingEntity)defender).getEntity();
            		dlc.l.a(dlc, (byte)2);
            		((LivingEntity)defender).setHealth(hp);
            		return true; 	
            	}
        	}
    	}
    	return false;
    }
    
    public boolean onExplode(Block block) {
    	int durability = SkillsProperties.getBlockDurability(block);
    	durability -= 2;
    	if(durability > 0){
    		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
	public boolean onBlockBreak(Player player, Block block) {
		SkillsPlayer sp = SkillsPlayer.get(player);
		int skill = SkillsProperties.getDestroySkill(block.getType());
		if(skill < 1)
			return false;
		int tool = SkillsProperties.getItemLevel(player.getItemInHand(), skill);
		int durability = SkillsProperties.getBlockDurability(block);
		if(SkillsProperties.debugOn){
			player.sendMessage("Skill - " + Integer.toString(sp.getLevel(skill)) + " Tool - " + Integer.toString(tool) + " Dura - " + Integer.toString(durability));
		}
		if(durability - sp.getLevel(skill) - tool > SkillsProperties.toBroke) {
			player.sendMessage("This block is too hard for you!");
		}
		else {
			if(sp.getLevel(skill) + tool >= durability) {
				sp.giveExp(skill, durability);
				return false;
			}
			else {
				durability -= 1;
				sp.giveExp(skill, 1);
				etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
				return true;
			}
		}
		return true;
	}

	public boolean onBlockPlace(Player player, Block block, Block blockClicked, Item itemInHand) {
		SkillsPlayer sp = SkillsPlayer.get(player);
		int skill = SkillsProperties.getCreateSkill(block.getType());
		if(skill < 1)
			return false;
		if(SkillsProperties.debugOn){
			player.sendMessage("Skill - " + Integer.toString(sp.getLevel(skill)));
		}
		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), sp.getLevel(skill));
		sp.giveExp(skill, 1);
		return false;
	}

	public boolean onConsoleCommand(String[] split)
	{
	      if (split[0].equalsIgnoreCase("stop"))
	      {
	         SkillsPlayer.save();
	      }
	      return false;
	}
	
	public boolean onCommand(Player player, String[] split) {
		if(split[0].equalsIgnoreCase("/skillinfo") && player.canUseCommand("/skillinfo")) {	
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
								sDestroy += Item.Type.fromId(i);
							}
							else{
								sDestroy += ", "+Item.Type.fromId(i);
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
								sCreate += Item.Type.fromId(i);
							}
							else{
								sCreate += ", "+Item.Type.fromId(i);
							}
							
						}
					}
					if(bCreate){
						player.sendMessage(sCreate);
					}
					//TODO: Combat-related skills description? Maybe later...
					return true;
				}
			}
			player.sendMessage("Correct usage is: /skillinfo [skillname]");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/skills") && player.canUseCommand("/skills")) {	
			Player p;
			if(split.length > 1 && player.canUseCommand("/skillsother")){
				p = etc.getServer().getPlayer(split[1]);
			}
			else{
				p = player;
			}
			if(p != null){
				SkillsPlayer sp = SkillsPlayer.get(p);
				player.sendMessage(sp.getName() + " are:");
				for(int skill = 1; skill < SkillsProperties.Skills.length; skill++) {
					int level = sp.getLevel(skill);
					if(level < SkillsProperties.Skills.length - 1){
						player.sendMessage(SkillsProperties.getRangForLevel(level, skill) + " " + SkillsProperties.Skills[skill] + "; level - " + level + "; exp - " + sp.getExp(skill) + "(" + SkillsProperties.getExpForLevel(level + 1, skill) + ");");
					}
					else{
						player.sendMessage(SkillsProperties.getRangForLevel(level, skill) + " " + SkillsProperties.Skills[skill] + "; level - " + level + "; exp - " + sp.getExp(skill) + ";");
					}
				}
				return true;
			}
			player.sendMessage("Correct usage is: /skills <playername>");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/reset") && player.canUseCommand("/reset")) {
			Player p;
			if(split.length > 1){
				p = etc.getServer().getPlayer(split[1]);
			}
			else{
				p = player;
			}
			if(p != null){
				SkillsPlayer sp = SkillsPlayer.get(p);
				for(int i = 1; i < SkillsProperties.Skills.length; i++) {
					sp.setExp(i, 0);
				}
				player.sendMessage("Done!");
				return true;
			}
			player.sendMessage("Correct usage is: /reset <playername>");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/giveexp") && player.canUseCommand("/giveexp")) {
			if(split.length > 3){
				Player p = etc.getServer().getPlayer(split[1]);
				if(p != null){
					SkillsPlayer sp = SkillsPlayer.get(p);
					int skill = 0;
					int amount = Integer.parseInt(split[3]);
					for(int i = 1; i < SkillsProperties.Skills.length; i++) {
						if(SkillsProperties.Skills[i].regionMatches(true, 0, split[2], 0, split[2].length())) {
							skill = i;
							break;
						}
					}
					if(amount != 0 && skill > 0 && sp != null) {
						sp.giveExp(skill, amount);
						player.sendMessage("Done!");
						return true;
					}
				}
			}
			player.sendMessage("Correct usage is: /giveexp [playername] [skillname] [amount]");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/setmods") && player.canUseCommand("/setmods")) {
			if(split.length > 2) {
				if(Double.valueOf(split[1]) != 0 && Double.valueOf(split[2]) != 0){
					SkillsProperties.weaponMod = Double.valueOf(split[1]);
					SkillsProperties.armorMod = Double.valueOf(split[2]);
					player.sendMessage("Done!");
					return true;
				}
			}
			player.sendMessage("Correct usage is: /setmods [weapon] [armor]");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/debug") && player.canUseCommand("/debug")) {
			if(SkillsProperties.debugOn){
				SkillsProperties.debugOn = false;
				player.sendMessage("Debug messages is now not showing.");
			}
			else{
				SkillsProperties.debugOn = true;
				player.sendMessage("Debug messages is now showing.");
			}
			return true;
		}
		return false;
	}
}
