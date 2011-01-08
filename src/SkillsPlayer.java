import java.util.Enumeration;
import java.util.Hashtable;

public class SkillsPlayer {
	
	private static final PropertiesFile playersFile = new PropertiesFile("Skills.txt");
	private static Hashtable<Player, SkillsPlayer> playersList = new Hashtable<Player, SkillsPlayer>();
	
	private Player player;
	private int[] skillExp = new int[100];
	
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
			return false;
		}
	}
	
	private SkillsPlayer(Player player, String skills) {
		this.player = player;
		String[] s = skills.split(":");
		for(int i = 0; i < s.length; i++) {
			this.skillExp[i+1] = Integer.parseInt(s[i]);
		}
	}

	private SkillsPlayer(Player player) {
		this.player = player;
	}

	public String getName() {
		return this.player.getName();
	}

	public int getLevel(int skill) {
		return SkillsProperties.getLevelFromExp(this.skillExp[skill], skill);
	}

	public int getExp(int skill) {
		return this.skillExp[skill];
	}
	
	public void setExp(int skill, int value) {
		this.skillExp[skill] = value;
	}
	
	public void giveExp(int skill, int value) {
		int before = this.getLevel(skill);
		if(this.skillExp[skill] + value < 0){
			this.skillExp[skill] = 0;
		}
		else {
			this.skillExp[skill] += value;
		}
		if(this.getLevel(skill) != before) {
			this.player.sendMessage("Congratulations! You are " + SkillsProperties.getRangForLevel(this.getLevel(skill), skill) + " " + SkillsProperties.Skills[skill] + "!");
		}
	}
}
