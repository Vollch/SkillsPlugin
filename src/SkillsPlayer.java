import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

public class SkillsPlayer {
	private static final PropertiesFile playersFile = new PropertiesFile("Skills.txt");
	private static Hashtable<Player, SkillsPlayer> playersList = new Hashtable<Player, SkillsPlayer>();
	
	private Player player;
	private Date battleDelay = new Date(0);
	private int[] skillExp = new int[100];
	private int[] skillLevel = new int[100];
	
	public static SkillsPlayer get(Player player){
		if(playersList.containsKey(player)) {
			return playersList.get(player);
		}
		try {
			playersFile.load();
			if(playersFile.containsKey(player.getName())) {
				String skills = playersFile.getString(player.getName());
				playersList.put(player, new SkillsPlayer(player, skills));
			}
			else {
				playersList.put(player, new SkillsPlayer(player));
			}
			return playersList.get(player);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean save(){
		try {
			playersFile.load();
			Enumeration<Player> keys = playersList.keys();
			while(keys.hasMoreElements()) {
				SkillsPlayer sp = playersList.get(keys.nextElement());
				String skills = "";
				for(int i = 1; i < SkillsProperties.Skills.length; i++) {
					if(i > 1) {
						skills += ":";
					}
					skills += String.valueOf(sp.getExp(i));
				}
				playersFile.setString(sp.getName(), skills);
			}
			playersFile.save();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private SkillsPlayer(Player player, String skills) {
		this.player = player;
		String[] s = skills.split(":");
		for(int skill = 0; skill < s.length; skill++) {
			this.skillExp[skill+1] = Integer.parseInt(s[skill]);
		}
		for(int skill = 1; skill < SkillsProperties.Skills.length; skill++){
			for(int level = 15; level > 0; level--) {
				if(skillExp[skill] >= SkillsProperties.getExpForLevel(level, skill)){
					this.skillLevel[skill] = level;
					break;
				}
			}
		}
	}

	private SkillsPlayer(Player player) {
		this.player = player;
		for(int i = 1; i < SkillsProperties.Skills.length; i++){
			this.skillLevel[i] = 1;
		}
	}

	public long getTimer(){
		return new Date().getTime() - this.battleDelay.getTime();
	}
	
	public void resetTimer(){
		this.battleDelay.setTime(new Date().getTime());
	}
	
	public void stopTimer(){
		this.battleDelay.setTime(0);
	}
	
	public String getName() {
		return this.player.getName();
	}

	public int getLevel(int skill) {
		return this.skillLevel[skill];
	}

	public void setLevel(int skill, int value) {
		this.skillLevel[skill] = value;
	}
	
	public int getExp(int skill) {
		return this.skillExp[skill];
	}
	
	public void setExp(int skill, int value) {
		this.skillExp[skill] = value;
	}
	
	public void giveExp(int skill, int value) {
		if(this.skillExp[skill] + value < 0)
			this.skillExp[skill] = 0;
		else 
			this.skillExp[skill] += value;
		
		if(this.skillLevel[skill] < 15 && this.skillExp[skill] >= SkillsProperties.getExpForLevel(this.skillLevel[skill]+1, skill)) {
			this.skillLevel[skill]++;
			
			this.player.sendMessage("Congratulations! You are " + SkillsProperties.getRangForLevel(this.getLevel(skill), skill) + " " + SkillsProperties.Skills[skill] + "!");
		}
	}
}
