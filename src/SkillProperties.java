import java.io.IOException;
import java.util.logging.Logger;

public class SkillProperties {
	static final Logger log = Logger.getLogger("Minecraft");
	public int[] DestroySkill = new int[100];
	public int[] CreateSkill = new int[100];
	public int[] Durability = new int[100];
	public String[] Skills;
	public String[] Rang;
	public int[] Exp;
	public int[][] Tools;
	public int[] WeaponsSkill = new int[400]; 
	public int[] WeaponsDamage = new int[400];
	public int[] ArmorsSkill = new int[400];
	public int[] ArmorsDefense = new int[400];
	public int[] ArmorsDurability = new int[400];
	public double[] Dodge;
	public int baseDurability;
	public int toBroke;
	public int saveTimer;
	public double weaponMod;
	public double armorMod;

	public SkillProperties() {
		if(!this.LoadConfig()) {
			this.Skills = new String[0];
			this.Rang = new String[0];
			this.Exp = new int[1];
			this.Exp[0] = 50000;
			this.Tools = new int[0][0];
			this.DefaultConfig();
		}
	}

	public boolean LoadConfig() {
		try {
			PropertiesFile props = new PropertiesFile("Skills.properties");
			String[] temp;
			props.load();
			this.baseDurability = props.getInt("base-durability", 1);
			this.toBroke = props.getInt("to-broke", 5);
			this.saveTimer = props.getInt("save-timer", 30000);
			this.weaponMod = props.getDouble("weapon-mod", 0.3);
			this.armorMod = props.getDouble("armor-mod", 1);
			if(!props.containsKey("Durability"))
				return false;
			temp = props.getString("Durability").split(",");
			for(String str : temp) {
				String[] temp2 = str.split("-");
				this.Durability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
			}
			if(!props.containsKey("Rang"))
				return false;
			temp = props.getString("Rang").split(",");
			Rang = new String[temp.length + 1];
			for(int i = 0; i < temp.length; i++) {
				Rang[i + 1] = temp[i];
			}
			if(!props.containsKey("Exp"))
				return false;
			temp = props.getString("Exp").split(",");
			Exp = new int[temp.length + 1];
			for(int i = 0; i < temp.length; i++) {
				Exp[i + 1] = Integer.parseInt(temp[i]);
			}
			if(!props.containsKey("SkillNames"))
				return false;
			temp = props.getString("SkillNames").split(",");
			this.Skills = new String[temp.length + 1];
			this.Tools = new int[temp.length][400];
			for(int i = 0; i < temp.length; i++) {
				Skills[i+1] = temp[i];
			}
			for(int i = 1; i < Skills.length; i++) {
				if(props.containsKey(Skills[i] + "Destroy")) {
					temp = props.getString(Skills[i] + "Destroy").split(",");
					for(String str : temp) {
						DestroySkill[Integer.parseInt(str)] = i;
					}
				}
				if(props.containsKey(Skills[i] + "Create")) {
					temp = props.getString(Skills[i] + "Create").split(",");
					for(String str : temp) {
						CreateSkill[Integer.parseInt(str)] = i;
					}
				}
				if(props.containsKey(Skills[i] + "Tools")) {
					temp = props.getString(Skills[i] + "Tools").split(",");
					for(String str : temp) {
						String[] temp2 = str.split("-");
						this.Tools[i][Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(props.containsKey(Skills[i] + "Weapons")) {
					temp = props.getString(Skills[i] + "Weapons").split(",");
					for(String str : temp) {
						String[] temp2 = str.split("-");
						this.WeaponsSkill[Integer.parseInt(temp2[0])] = i;
						this.WeaponsDamage[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(props.containsKey(Skills[i] + "Armors")) {
					temp = props.getString(Skills[i] + "Armors").split(",");
					for(String str : temp) {
						String[] temp2 = str.split("-");
						this.ArmorsSkill[Integer.parseInt(temp2[0])] = i;
						this.ArmorsDefense[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[2]);
						this.ArmorsDurability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(props.containsKey(Skills[i] + "Dodge")) {
					temp = props.getString(Skills[i] + "Dodge").split(",");
					this.Dodge = new double[temp.length + 1];
					this.Dodge[0] = i;
					for(int ii = 0; ii < temp.length; ii++) {
						this.Dodge[ii+1] = Double.valueOf(temp[ii]);
					}
				}
			}
			return true;
		}
		catch(IOException ioe) {
			return false;
		}
	}

	public void DefaultConfig() {
		try {
			PropertiesFile props = new PropertiesFile("Skills.properties");
			props = new PropertiesFile("Skills.properties");
			props.load();
			props.setInt("base-durability", 1);
			props.setInt("to-broke", 5);
			props.setInt("save-timer", 30000);
			props.save();
		}
		catch(IOException ioe) {}
	}

	public int GetDestroySkill(Block block) {
		return this.DestroySkill[block.getType()];
	}

	public int GetToolLevel(int ItemInHand, int skill) {
		if(ItemInHand == -1)
			return 0;
		return this.Tools[skill][ItemInHand];
	}
	
	public int GetDurability(Block block) {
		int durability = block.getData();
		if(durability == 0) {
			if(this.Durability[block.getType()] > 0) {
				durability = this.Durability[block.getType()];
			}
			else {
				durability = this.baseDurability;
			}
			etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
		}
		return durability;
	}

	public int GetCreateSkill(Block block) {
		return this.CreateSkill[block.getType()];
	}

	public int GetLevelFromExp(int expirence) {
		for(int i = this.Exp.length - 1; i > 0; i--) {
			if(expirence >= this.Exp[i])
				return i;
		}
		return 0;
	}

	public String GetRangFromLevel(int level) {
		if(level < this.Rang.length)
			return this.Rang[level];
		else
			return "";
	}
}
