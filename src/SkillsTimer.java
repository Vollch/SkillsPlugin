import java.util.TimerTask;
import java.util.logging.Logger;

public class SkillsTimer extends TimerTask {
	private static final Logger log = Logger.getLogger("Minecraft");

	public SkillsTimer() {
	}

	public void run() {
        if(!SkillsPlayer.save()){
        	log.warning("Something wrong! Can't save skill!");
         }
	}
}
