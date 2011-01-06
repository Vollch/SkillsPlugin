public class SkillsListener extends PluginListener {
	public boolean onBlockBreak(Player player, Block block) {
		SkillsPlayer sp = SkillsPlayer.get(player);
		int skill = SkillsProperties.GetDestroySkill(block);
		if(skill < 1)
			return false;
		int tool = SkillsProperties.GetToolLevel(player.getItemInHand(), skill);
		int durability = SkillsProperties.GetDurability(block);
		if(SkillsProperties.debug){
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
				durability -= sp.getLevel(skill);
				durability -= tool;
				sp.giveExp(skill, sp.getLevel(skill));
				etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
				return true;
			}
		}
		return true;
	}

	public boolean onBlockPlace(Player player, Block block, Block blockClicked, Item itemInHand) {
		SkillsPlayer sp = SkillsPlayer.get(player);
		int skill = SkillsProperties.GetCreateSkill(block);
		if(skill < 1)
			return false;
		if(SkillsProperties.debug){
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
		if(split[0].equalsIgnoreCase("/skills") && player.canUseCommand("/skills")) {	
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
				for(int i = 1; i < SkillsProperties.Skills.length; i++) {
					int level = sp.getLevel(i);
					if(level < SkillsProperties.Skills.length - 1){
						player.sendMessage(SkillsProperties.GetRangFromLevel(level) + " " + SkillsProperties.Skills[i] + "; level - " + level + "; exp - " + sp.getExp(i) + "(" + SkillsProperties.Exp[level + 1] + ");");
					}
					else{
						player.sendMessage(SkillsProperties.GetRangFromLevel(level) + " " + SkillsProperties.Skills[i] + "; level - " + level + "; exp - " + sp.getExp(i) + ";");
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
					sp.setExp(i, SkillsProperties.Exp[1]);
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
						if(SkillsProperties.Skills[i].equalsIgnoreCase(split[2])) {
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
			if(SkillsProperties.debug){
				SkillsProperties.debug = false;
				player.sendMessage("Debug messages is now not showing.");
			}
			else{
				SkillsProperties.debug = true;
				player.sendMessage("Debug messages is now showing.");
			}
			return true;
		}
		return false;
	}
	
    public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
    	if(!SkillsProperties.combat)
    		return false;
    	
    	if(attacker.isPlayer()){
        	int weapon = attacker.getPlayer().getItemInHand();
        	if(weapon == -1){
        		weapon = 399;
        	}
        	
        	int wskill = SkillsProperties.WeaponsSkill[weapon];
        	if(wskill == 0){
        		attacker.getPlayer().sendMessage("You can't fight with such thing in hands!");
        		return true;
        	}
        	
        	if(!defender.isPlayer()){
        		SkillsPlayer.get(attacker.getPlayer()).giveExp(wskill, 1);
        		return false;
        	}
        	else
        	{
            	double dodge = SkillsProperties.Dodge[SkillsPlayer.get(defender.getPlayer()).getLevel((int)SkillsProperties.Dodge[0])];
            	if(Math.random() < dodge){
            		SkillsPlayer.get(defender.getPlayer()).giveExp((int)SkillsProperties.Dodge[0], 1);
            		attacker.getPlayer().sendMessage("Enemy dodged!");
            		return true;
            	}
        	}
            	
        	int level = SkillsPlayer.get(attacker.getPlayer()).getLevel(wskill);
        	double hit = SkillsProperties.WeaponsDamage[weapon] * (1 + (level * SkillsProperties.weaponMod));
        	Inventory inv = defender.getPlayer().getInventory();
        	
        	double def = 0;
        	for(int i = 36; i < 40; i++){
        		Item it = inv.getItemFromSlot(i);
        		if(it != null){
        			int askill = SkillsProperties.ArmorsSkill[it.getItemId()];
        			if(askill > 0){
        				SkillsPlayer.get(defender.getPlayer()).giveExp(askill, 1);
            			def += SkillsProperties.ArmorsDefense[it.getItemId()];
            			int ahit = (int)hit - SkillsPlayer.get(defender.getPlayer()).getLevel(askill);
            			if(ahit > 0){
            				if(it.getDamage() + ahit < SkillsProperties.ArmorsDurability[it.getItemId()]){
            					inv.setSlot(it.getItemId(), 1, it.getDamage() + ahit, i);
            				}
            				else
            				{
            					inv.removeItem(i);
            				}
            			}
        			}
        		}
        	}
        	inv.update();
        	
        	def *= SkillsProperties.armorMod;
        	
        	if(hit > def){
        		SkillsPlayer.get(attacker.getPlayer()).giveExp(wskill, (int)hit - (int)def);
        		int hp = defender.getPlayer().getHealth() - ((int)hit - (int)def);
        		
            	lc anim = defender.getPlayer().getEntity();
            	if(hp < 1){
            		//anim.l.a(anim, (byte)3);
            		defender.getPlayer().setHealth(1);
            		return false;
            	}
            	else
            	{
            		anim.l.a(anim, (byte)2);
            		defender.getPlayer().setHealth(hp);
            	}
        	}
        	if(SkillsProperties.debug){
        		attacker.getPlayer().sendMessage("Hit: "+(double)hit+" Def: "+(double)def);
        	}
        	return true;
    	}
    	else if(defender.isPlayer()){
        	double dodge = SkillsProperties.Dodge[SkillsPlayer.get(defender.getPlayer()).getLevel((int)SkillsProperties.Dodge[0])];
        	if(Math.random() < dodge){
        		SkillsPlayer.get(defender.getPlayer()).giveExp((int)SkillsProperties.Dodge[0], 1);
        		return true;
        	} 
    	}
    	return false;
    }
}
