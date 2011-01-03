public class SkillsPlayer {
	private SkillsListener parent;
	private Player player;
	private int[] skillExp = new int[20];
	
	public SkillsPlayer(SkillsListener parent, Player player, String skills)
	{	
		this.parent = parent;
		this.player = player;
		String[] s = skills.split(":");
	    for(int i = 0; i < s.length; i++){
	    	this.skillExp[i+1] = Integer.parseInt(s[i]);
	    }
	}
	public SkillsPlayer(SkillsListener parent, Player player)
	{
		this.parent = parent;
		this.player = player;
		for(int i = 1; i <= this.parent.skillsCount; i++){
			this.skillExp[i] = 0;
		}
	}
	
	public String getName(){
		return this.player.getName();
	}
	
	public int getLevel(int skill)
	{
		for(int i = 15; i > 0; i--){
			if(this.skillExp[skill] >= this.parent.exp[i]){
				return i;
			}
		}
		return 1;
	}
	
	public int getExp(int skill)
	{
		return this.skillExp[skill];
	}
	
	public void giveExp(int skill, int value)
	{
		int before = this.getLevel(skill);
		this.skillExp[skill] += value;
		if(this.getLevel(skill) > before){
			this.player.sendMessage("Congratulations! You are "+this.parent.rang[this.getLevel(skill)]+" "+this.parent.skills[skill]+"!");
		}
	}
}
