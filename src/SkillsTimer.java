import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Logger;


public class SkillsTimer extends TimerTask{
	static final Logger log = Logger.getLogger("Minecraft");
	SkillsListener parent;
	
	public SkillsTimer(SkillsListener parent)
	{
	    this.parent = parent;
	}
	
	public void run(){
		try {
			parent.players = new PropertiesFile("Skills.txt");
			parent.players.load();
			for (SkillsPlayer sp : parent.playerList){
				String skills = "";
				for(int i = 1; i <= this.parent.skills_count; i++){
					if(i > 1){
						skills = skills + ":";
					}
					skills = skills+String.valueOf(sp.getExp(i));
				}
				parent.players.setString(sp.getName(), skills);
			}
			parent.players.save();
		} catch (IOException ioe) {}
	}
}