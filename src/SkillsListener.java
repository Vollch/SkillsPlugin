import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Logger;

public class SkillsListener extends PluginListener {
	static final Logger log = Logger.getLogger("Minecraft");
	public PropertiesFile properties;
	public PropertiesFile players;
	public ArrayList<SkillsPlayer> playerList = new ArrayList<SkillsPlayer>();
	public Timer timer;
	
	public int[] type2skill = new int[200];
	public int[] durability = new int[100];
	public String[] skills = new String[20];
	public String[] rang = new String[16];
	public int[] exp = new int[16];
	public int[][] tools = new int[20][300];

	
	public int skills_count;
	
	int basedurability;
	int tobroke;
	int savetimer;	
	
	public SkillsListener() {
		int index = 0;
		int skill = 0;
	    try {
		    this.properties = new PropertiesFile("Skills.properties");
		    this.properties.load();
	        this.basedurability = this.properties.getInt("base-durability", 1);
	        this.tobroke = this.properties.getInt("to-broke", 5);
	        this.savetimer = this.properties.getInt("save-timer", 30000);
	        for(int i = 1; i <= 20;i++){
	        	if(!this.properties.containsKey("Skill"+String.valueOf(i))){
	        		break;
	        	}
	        	String[] s = this.properties.getString("Skill"+String.valueOf(i)).split(":");
	        	for(int ii = 1; ii <= 20; ii++){
	        		if(this.skills[ii] == null){
	        			this.skills[ii] = s[0];
	        			this.skills_count = ii;
	        			skill = ii;
	        			break;
	        		}
	        		else if(this.skills[ii].equals(s[0])){
	        			skill = ii;
	        			break;
	        		}
	        	}
	        	for(int ii = 2; ii < s.length; ii++){
	        		index = Integer.parseInt(s[ii]) + Integer.parseInt(s[1]);
	        		this.type2skill[index] = skill;
	        	}
	        	if(this.properties.containsKey("Tools"+String.valueOf(i))){
	        		String[] ss = this.properties.getString("Tools"+String.valueOf(i)).split(":");
	        		for(int ii = 0; ii < ss.length; ii+=2){
	        			this.tools[skill][Integer.parseInt(ss[ii])] = Integer.parseInt(ss[ii+1]); 
	        		}
	        	}
	        }
	        String[] s2 = this.properties.getString("Durability").split(":");
	        for(int i = 0; i < s2.length; i+=2){
	        	this.durability[Integer.parseInt(s2[i])] = Integer.parseInt(s2[i+1]);
	        }
	        String[] s3 = this.properties.getString("Rang").split(":");
	        for(int i = 0; i < s3.length; i++){
	        	this.rang[i+1] = s3[i];
	        }
	        String[] s4 = this.properties.getString("Exp").split(":");
	        for(int i = 0; i < s4.length; i++){
	        	this.exp[i+1] = Integer.parseInt(s4[i]);
	        }
	    } catch (IOException ioe) {}
	    this.timer = new Timer();
	    this.timer.scheduleAtFixedRate(new SkillsTimer(this), 0L, this.savetimer);
	}
	
	public void onLogin(Player player) {
		for (SkillsPlayer sp : playerList){
			if(player.getName().equals(sp.getName())){
				return;
			}	
		}
	    try {
	    	this.players = new PropertiesFile("Skills.txt");
			this.players.load();
			if(this.players.containsKey(player.getName())){
				String skills = this.players.getString(player.getName());
				playerList.add(new SkillsPlayer(this, player, skills));
			}
			else
			{
				playerList.add(new SkillsPlayer(this, player));
			} 
		} catch (IOException ioe) {}
	}
	
	public boolean onBlockBreak(Player player, Block block) {
		int durability = block.getData();
		SkillsPlayer sp = null;
		int skill = 0;
		
		if(this.type2skill[block.getType()] > 0){
			skill = this.type2skill[block.getType()];
		}
		else
		{
			return false;
		}
		
		int tool = this.tools[skill][player.getItemInHand()];

		if(durability == 0){
			if(this.durability[block.getType()] > 0){
				durability = this.durability[block.getType()];
			}
			else
			{
				durability = this.basedurability;
			}
			etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
		}
		for (SkillsPlayer p : playerList){
			if(player.getName().equals(p.getName())){
				sp = p;
				break;
			}	
		}
		if(durability - sp.getLevel(skill) - tool > this.tobroke){
			player.sendMessage("This block is too hard for you!");
		}
		else
		{
			if(sp.getLevel(skill) + tool >= durability){
				sp.giveExp(skill, durability);
				return false;
			}
			else
			{
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
		SkillsPlayer sp = null;
		int skill = 0;
		
		
		if(this.type2skill[block.getType() + 100] > 0){
			skill = this.type2skill[block.getType() + 100];
		}
		else
		{
			return false;
		}
		
		for (SkillsPlayer p : playerList){
			if(player.getName().equals(p.getName())){
				sp = p;
				break;
			}	
		}
		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), sp.getLevel(skill));
		sp.giveExp(skill, 1);
		return false;
    }
    
    public boolean onCommand(Player player, String[] split)
    {
        if ((split[0].equalsIgnoreCase("/exp")) && (player.canUseCommand("/exp")))
        {
        	SkillsPlayer sp = null;
        	for (SkillsPlayer p : playerList){
    			if(player.getName().equals(p.getName())){
    				sp = p;
    				break;
    			}	
    		}
            player.sendMessage(sp.getName()+" are:");
            for(int i = 1; i <= this.skills_count; i++){
            	player.sendMessage(this.rang[sp.getLevel(i)]+" "+this.skills[i]+"; level - "+sp.getLevel(i)+"; exp - "+sp.getExp(i)+";");
            }
            return true;
        }
        else if ((split[0].equalsIgnoreCase("/giveexp")) && (player.canUseCommand("/giveexp")))
        {
        	try
        	{
        		SkillsPlayer sp = null;
        		int skill = 0;
        		int amount = Integer.parseInt(split[3]); 
        		for (SkillsPlayer p : playerList){
        			if(split[1].equalsIgnoreCase(p.getName())){
        				sp = p;
        				break;
        			}
        		}
        		for(int i = 1; i <= this.skills_count; i++){
        			if(skills[i].equalsIgnoreCase(split[2])){
        				skill = i;
        				break;
        			}
        		}
        		if(amount != 0 && skill != 0 && sp != null){
        			sp.giveExp(skill, amount);
        			player.sendMessage("Done!");
        		}
        		else
        		{
        			player.sendMessage("Syntax: <playername> <skillname> <amount>");
        		}
        	}
        	catch (ArrayIndexOutOfBoundsException e)
        	{
        		player.sendMessage("Syntax: <playername> <skillname> <amount>");
        	}
        	return true;
        } 
    return false;
    }
}