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
				if(level < Props.Skills.length - 1){
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
		else if((split[0].equalsIgnoreCase("/setmods")) && (player.canUseCommand("/setmods"))) {
			try {
				if(Double.valueOf(split[1]) > 0 && Double.valueOf(split[2]) > 0){
					this.Props.weaponMod = Double.valueOf(split[1]);
					this.Props.armorMod = Double.valueOf(split[2]);
				}
				else {
					player.sendMessage("Syntax: <weapon modifier> <armor modifier>");
				}
			}
			catch(Exception e) {
				player.sendMessage("Syntax: <weapon modifier> <armor modifier>");
			}
			return true;
		}
		return false;
	}
	
    public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
    	if(attacker.isPlayer()){
        	int weapon = attacker.getPlayer().getItemInHand();
        	if(weapon == -1){
        		weapon = 399;
        	}
        	
        	int wskill = Props.WeaponsSkill[weapon];
        	if(wskill == 0){
        		attacker.getPlayer().sendMessage("You can't fight with such thing in hands!");
        		return true;
        	}
        	
        	if(!defender.isPlayer()){
        		this.playersList.get(attacker.getPlayer()).giveExp(wskill, 1);
        		return false;
        	}
        	else
        	{
            	double dodge = this.Props.Dodge[this.playersList.get(defender.getPlayer()).getLevel((int)this.Props.Dodge[0])];
            	if(Math.random() < dodge){
            		this.playersList.get(defender.getPlayer()).giveExp((int)this.Props.Dodge[0], 1);
            		attacker.getPlayer().sendMessage("Enemy dodged!");
            		return true;
            	}
        	}
            	
        	int level = this.playersList.get(attacker.getPlayer()).getLevel(wskill);
        	double hit = Props.WeaponsDamage[weapon] * (1 + (level * this.Props.weaponMod));
        	
        	Inventory inv = defender.getPlayer().getInventory();
        	double def = 0;
        	for(int i = 36; i < 40; i++){
        		Item it = inv.getItemFromSlot(i);
        		if(it != null){
        			int askill = this.Props.ArmorsSkill[it.getItemId()];
        			if(askill > 0){
             			this.playersList.get(defender.getPlayer()).giveExp(askill, 1);
            			def += this.Props.ArmorsDefense[it.getItemId()];
            			int ahit = (int)hit - this.playersList.get(defender.getPlayer()).getLevel(askill);
            			if(ahit > 0){
            				if(it.getDamage() + ahit < this.Props.ArmorsDurability[it.getItemId()]){
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
        	
        	def *= this.Props.armorMod;
        	
        	if(hit > def){
        		this.playersList.get(attacker.getPlayer()).giveExp(wskill, (int)hit - (int)def);
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
        	attacker.getPlayer().sendMessage("Hit: "+(double)hit+" Def: "+(double)def);
        	return true;
    	}
    	else if(defender.isPlayer()){
        	double dodge = this.Props.Dodge[this.playersList.get(defender.getPlayer()).getLevel((int)this.Props.Dodge[0])];
        	if(Math.random() < dodge){
        		this.playersList.get(defender.getPlayer()).giveExp((int)this.Props.Dodge[0], 1);
        		return true;
        	} 
    	}
    	return false;
    }
}
