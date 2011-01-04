import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.logging.Logger;

public class SkillsListener extends PluginListener {
	static final Logger log = Logger.getLogger("Minecraft");
	public SkillProperties Props;
	public PropertiesFile propertiesFile;
	public PropertiesFile playersFile;
	public Hashtable<Player, SkillsPlayer> playersList = new Hashtable<Player, SkillsPlayer>();
	public Timer timer;

	public SkillsListener() {
		this.Props = new SkillProperties();
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new SkillsTimer(this), 0L, Props.saveTimer);
	}

	public void onLogin(Player player) {
		if(this.playersList.containsKey(player)) {
			return;
		}
		try {
			this.playersFile = new PropertiesFile("Skills.txt");
			this.playersFile.load();
			if(this.playersFile.containsKey(player.getName())) {
				String skills = this.playersFile.getString(player.getName());
				this.playersList.put(player, new SkillsPlayer(this, player, skills));
			}
			else {
				this.playersList.put(player, new SkillsPlayer(this, player));
			}
		}
		catch(IOException ioe) {}
	}

	public boolean onBlockBreak(Player player, Block block) {
		SkillsPlayer sp = this.playersList.get(player);
		int skill = Props.GetDestroySkill(block);
		if(skill < 1)
			return false;
		int tool = Props.GetToolLevel(player.getItemInHand(), skill);
		int durability = Props.GetDurability(block);
		player.sendMessage("Skill - " + Integer.toString(sp.getLevel(skill)) + " Tool - " + Integer.toString(tool) + " Dura - " + Integer.toString(durability)+" Item - "+player.getItemInHand());
		if(durability - sp.getLevel(skill) - tool > Props.toBroke) {
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
		SkillsPlayer sp = this.playersList.get(player);
		int skill = Props.GetCreateSkill(block);
		if(skill < 1)
			return false;
		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), sp.getLevel(skill));
		sp.giveExp(skill, 1);
		return false;
	}

	public boolean onCommand(Player player, String[] split) {
		if((split[0].equalsIgnoreCase("/exp")) && (player.canUseCommand("/exp"))) {
			SkillsPlayer sp = this.playersList.get(player);
			player.sendMessage(player.getName() + " are:");
			for(int i = 1; i < Props.Skills.length; i++) {
				int level = sp.getLevel(i);
				if(level < Props.Exp.length - 1){
					player.sendMessage(Props.GetRangFromLevel(level) + " " + Props.Skills[i] + "; level - " + level + "; exp - " + sp.getExp(i) + "(" + Props.Exp[level + 1] + ");");
				}
				else
				{
					player.sendMessage(Props.GetRangFromLevel(level) + " " + Props.Skills[i] + "; level - " + level + "; exp - " + sp.getExp(i) + ";");
				}
			}
			return true;
		}
		else if((split[0].equalsIgnoreCase("/giveexp")) && (player.canUseCommand("/giveexp"))) {
			try {
				SkillsPlayer sp = this.playersList.get(etc.getServer().getPlayer(split[1]));
				int skill = 0;
				int amount = Integer.parseInt(split[3]);
				for(int i = 1; i < Props.Skills.length; i++) {
					if(Props.Skills[i].equalsIgnoreCase(split[2])) {
						skill = i;
						break;
					}
				}
				if(amount != 0 && skill > 0 && sp != null) {
					sp.giveExp(skill, amount);
					player.sendMessage("Done!");
				}
				else {
					player.sendMessage("Syntax: <playername> <skillname> <amount>");
				}
			}
			catch(Exception e) {
				player.sendMessage("Syntax: <playername> <skillname> <amount>");
			}
			return true;
		}
		return false;
	}
	
    public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
    	if(!attacker.isPlayer()){
    		return false;
    	}
    	int weapon = attacker.getPlayer().getItemInHand();
    	if(weapon == -1){
    		weapon = 399;
    	}
    	int skill = Props.WeaponSkill[weapon];
    	if(skill == 0){
    		attacker.getPlayer().sendMessage("You can't fight with such thing in hands!");
    		return true;
    	}
    	this.playersList.get(attacker.getPlayer()).giveExp(skill, 1);
    	if(!defender.isPlayer()){
    		return false;
    	}
    	//this.playersList.get(defender.getPlayer()).getLevel((int)this.Props.Dodge[0])
    	double dodge = this.Props.Dodge[1];
    	attacker.getPlayer().sendMessage("Enemy dodge: "+String.valueOf(dodge));
    	if(Math.random() < dodge){
    		this.playersList.get(defender.getPlayer()).giveExp((int)this.Props.Dodge[0], 1);
    		return true;
    	} 	
    	int damage = Props.WeaponDamage[weapon];
    	int level = this.playersList.get(attacker.getPlayer()).getLevel(skill);
    	double hit = damage * (1 + (level * this.Props.weaponMod));
    	double def = 0;
    	for(int i = 36; i < 40; i++){
    		Item it = defender.getPlayer().getInventory().getItemFromSlot(i);
    		if(it != null){
    			def += this.Props.ArmorDefense[it.getItemId()];
    			it.setDamage(it.getDamage() + (int)hit / 4);
    		}
    	}
    	def *= this.Props.armorMod;
    	if(hit > def){
    		int hp = defender.getPlayer().getHealth() - ((int)hit - (int)def);
    		defender.getPlayer().setHealth(hp);
    		
        	lc anim = defender.getPlayer().getEntity();
        	if(hp < 1){
        		anim.l.a(anim, (byte)3);
        	}
        	else
        	{
        		anim.l.a(anim, (byte)2);
        	}
        	
    	}
    	attacker.getPlayer().sendMessage("Hit: "+(double)hit+" Def: "+(double)def);
    	

    	return true;
    }
}
