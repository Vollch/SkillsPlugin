import java.util.TimerTask;

public class SkillsTimer extends TimerTask {
	public SkillsTimer() {
	}

	public void run() {
        SkillsPlayer.save();
	}
}
