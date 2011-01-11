import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class SkillsListener extends PluginListener {
	public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
		if(attacker.getHealth() < 1)
			return true;
		
    	if(defender.isPlayer()){
    		Player dplayer = defender.getPlayer();
    		if(SkillsPlayer.get(dplayer).getTimer() < 1000){
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
        	SkillsPlayer.get(dplayer).resetTimer();
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
            	
            	if(defender.isPlayer()){
            		hit = wdamage * (1 + (wlevel * SkillsProperties.weaponMod));
            	}
            	else
            	{
            		hit = wdamage * (1 + (wlevel * (SkillsProperties.weaponMod / 3)));
            	}
            	
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
            	
            	if(attacker.isPlayer()){
            		def *= SkillsProperties.armorMod;
            	}
            	else
            	{
            		def *= SkillsProperties.armorMod / 3;
            	}
            	
    		}
    		else
    		{
    			def = 0;
    		}
    		
        	if(SkillsProperties.debugOn && aplayer != null){
        		aplayer.sendMessage("Hit: "+(double)hit+" Def: "+(double)def+" From:"+defender.toString());
        	}
        	if(SkillsProperties.debugOn && dplayer != null){
        		dplayer.sendMessage("Def: "+(double)def+" Hit: "+(double)hit+" From:"+attacker.toString());
        	}
        	
    		int dmg = (int)hit - (int)def;       	
        	if(dmg > 0){
        		if(attacker.isPlayer()){
        			SkillsPlayer.get(aplayer).giveExp(wskill, dmg);
        		}
        		int hp = ((LivingEntity)defender).getHealth() - dmg;
        		
        		
    	    	if(hp > 0){    		
            		lc dlc = ((LivingEntity)defender).getEntity();
            		dlc.l.a(dlc, (byte)2);
            		((LivingEntity)defender).setHealth(hp);
    	    	}
    	    	else{
    	    		if(defender.isPlayer()){
    	    			dplayer = defender.getPlayer();
    	            	Inventory inv = dplayer.getInventory();
    	    			for(int i = 0; i < 40; i++)
    	    			{
    	    				if(inv.getItemFromSlot(i) != null){
    	    					etc.getServer().dropItem(dplayer.getX(), dplayer.getY(), dplayer.getZ(), inv.getItemFromSlot(i).getItemId(), inv.getItemFromSlot(i).getAmount());
    	    					inv.removeItem(i);
    	    				}
    	    			}
    	    			inv.update();
    	    			SkillsPlayer.get(dplayer).resetTimer();
    	    		}
               		lc dlc = ((LivingEntity)defender).getEntity();
            		dlc.l.a(dlc, (byte)3);
            		((LivingEntity)defender).setHealth(-1);
    	    	}
        	}
        	return true;
    	}
    	return false;
    }
    
    public boolean onExplode(Block center) {
    	HashSet<Block> list = new HashSet<Block>();
    	double mod = 0;
    	if(center.getStatus() == 1){
    		mod = 2.5;
    	}
    	else{
    		mod = 2;
    	}
        for (int count1 = 0; count1 < 16; count1++) {
            for (int count2 = 0; count2 < 16; count2++) {
                for (int count3 = 0; count3 < 16; count3++) {
                    if ((count1 != 0) && (count1 != 15) && (count2 != 0) && (count2 != 15) && (count3 != 0) && (count3 != 15)) {
                        continue;
                    }
                    double d1 = count1 / 15.0F * 2.0F - 1.0F;
                    double d2 = count2 / 15.0F * 2.0F - 1.0F;
                    double d3 = count3 / 15.0F * 2.0F - 1.0F;
                    double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
                    d1 /= d4;
                    d2 /= d4;
                    d3 /= d4;
                    double d5 = center.getX();
                    double d6 = center.getY();
                    double d7 = center.getZ();
                    double f2 = mod * (0.7D + Math.random() * 0.6D);
                    while(f2 > 0.0F) {
                        int x = ic.b(d5);
                        int y = ic.b(d6);
                        int z = ic.b(d7);
                        if(f2 > 0.0D){
                            list.add(new Block(0,x,y,z));
                        }
                        d5 += d1 * 0.3F;
                        d6 += d2 * 0.3F;
                        d7 += d3 * 0.3F;
                        f2 -= 0.3F * 0.75F;
                    }
                }
            }
        }
        
        Iterator<Block> itr = list.iterator();
        Random rand = new Random();
        while(itr.hasNext()){
        	Block block = itr.next();
        	block.refresh();
        	if(block.getType() == 0 || block.getType() == 7 || block.getType() == 51){
        		continue;
        	}
        	else if(block.getType() == 46){
        		block.setY(block.getY()+1);
        		block.refresh();
        		if(block.getType() == 0){
        			block.setType(51);
        			block.update();
        		}
        	}
        	else{
        		int durability = SkillsProperties.getBlockDurability(block);
        		double x = Math.pow(center.getX() - block.getX(), 2);
        	    double y = Math.pow(center.getY() - block.getY(), 2);
        	    double z = Math.pow(center.getZ() - block.getZ(), 2);
        	    durability -= mod * 2 - Math.sqrt(x + y + z);
        	    if(durability < 1){
        	    	if(gv.m[block.getType()].a(rand) > 0){
        	    		etc.getServer().dropItem(block.getX(), block.getY(), block.getZ(), gv.m[block.getType()].a(0, rand), gv.m[block.getType()].a(rand));
        	    	}
        	    	block.setType(0);
        	    	block.update();
        	    }
        	    else
        	    {
        	    	etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
        	    }
        	}
        }
    	
    	for (LivingEntity le : etc.getServer().getLivingEntityList()){
    		double x = Math.pow(center.getX() - le.getX(), 2);
    	    double y = Math.pow(center.getY() - le.getY(), 2);
    	    double z = Math.pow(center.getZ() - le.getZ(), 2);
    	    double dist = Math.sqrt(x + y + z);
    	    if(dist < mod * 4){
    	    	int hp = le.getHealth() - (int)(mod * 10 - dist * 3);
    	    	if(hp > 0){    		
            		lc dlc = le.getEntity();
            		dlc.l.a(dlc, (byte)2);
            		le.setHealth(hp);
    	    	}
    	    	else{
    	    		if(le.isPlayer()){
    	    			Inventory inv = le.getPlayer().getInventory();
    	    			for(int i = 0; i < 40; i++)
    	    			{
    	    			    if(inv.getItemFromSlot(i) != null){
    	    			    	etc.getServer().dropItem(le.getPlayer().getX(), le.getPlayer().getY(), le.getPlayer().getZ(), inv.getItemFromSlot(i).getItemId(), inv.getItemFromSlot(i).getAmount());
        	    			    inv.removeItem(i);
    	    			    }
    	    			}
    	    			inv.update();
    	    			SkillsPlayer.get(le.getPlayer()).resetTimer();
    	    		}
               		lc dlc = le.getEntity();
            		dlc.l.a(dlc, (byte)3);
            		le.setHealth(-1);
    	    	}
    	    }
    	}
    	return true;
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
				etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
				sp.giveExp(skill, 1);
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
		if(split[0].equalsIgnoreCase("/spawn") || split[0].equalsIgnoreCase("/home")) {	
    		if(SkillsPlayer.get(player).getTimer() < 60000){
    			player.sendMessage("You can't teleport during battle!");
    			return true;
    		}	
		}
		else if(split[0].equalsIgnoreCase("/skillinfo") && player.canUseCommand("/skillinfo")) {	
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
					//TODO: Combat-related skills description
					return true;
				}
			}
			player.sendMessage("Correct usage is: /skillinfo [skillname]");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/skills") && player.canUseCommand("/skills")) {	
			Player p;
			if(split.length > 1 && player.canUseCommand("/skillsother")){
				p = etc.getServer().matchPlayer(split[1]);
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
				return true;
			}
			player.sendMessage("Correct usage is: /skills <playername>");
			return true;
		}
		else if(split[0].equalsIgnoreCase("/reset") && player.canUseCommand("/reset")) {
			Player p;
			if(split.length > 1){
				p = etc.getServer().matchPlayer(split[1]);
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
				Player p = etc.getServer().matchPlayer(split[1]);
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
