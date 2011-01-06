public class SkillsProperties {
	private static final PropertiesFile propertiesFile = new PropertiesFile("Skills.properties");
	public static int[] DestroySkill = new int[100];
	public static int[] CreateSkill = new int[100];
	public static int[] Durability = new int[100];
	public static String[] Skills;
	public static String[] Rang;
	public static int[] Exp;
	public static int[][] Tools;
	public static int[] WeaponsSkill = new int[400]; 
	public static int[] WeaponsDamage = new int[400];
	public static int[] ArmorsSkill = new int[400];
	public static int[] ArmorsDefense = new int[400];
	public static int[] ArmorsDurability = new int[400];
	public static double[] Dodge;
	public static int baseDurability;
	public static int toBroke;
	public static int saveTimer;
	public static boolean debug;
	public static boolean combat;
	public static double weaponMod;
	public static double armorMod;

	public static boolean loadConfig() {
		try {
			String[] temp;
			propertiesFile.load();
			debug = propertiesFile.getBoolean("debug");
			combat = propertiesFile.getBoolean("combat");
			baseDurability = propertiesFile.getInt("baseDurability");
			toBroke = propertiesFile.getInt("toBroke");
			saveTimer = propertiesFile.getInt("saveTimer");
			weaponMod = propertiesFile.getDouble("weaponMod");
			armorMod = propertiesFile.getDouble("armorMod");
			if(!propertiesFile.containsKey("Durability"))
				return false;
			temp = propertiesFile.getString("Durability").split(",");
			for(String str : temp) {
				String[] temp2 = str.split("-");
				Durability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
			}
			if(!propertiesFile.containsKey("Rang"))
				return false;
			temp = propertiesFile.getString("Rang").split(",");
			Rang = new String[temp.length + 1];
			for(int i = 0; i < temp.length; i++) {
				Rang[i + 1] = temp[i];
			}
			if(!propertiesFile.containsKey("Exp"))
				return false;
			temp = propertiesFile.getString("Exp").split(",");
			Exp = new int[temp.length + 1];
			for(int i = 0; i < temp.length; i++) {
				Exp[i + 1] = Integer.parseInt(temp[i]);
			}
			if(!propertiesFile.containsKey("SkillNames"))
				return false;
			temp = propertiesFile.getString("SkillNames").split(",");
			Skills = new String[temp.length + 1];
			Tools = new int[temp.length][400];
			for(int i = 0; i < temp.length; i++) {
				Skills[i+1] = temp[i];
			}
			for(int i = 1; i < Skills.length; i++) {
				if(propertiesFile.containsKey(Skills[i] + "Destroy")) {
					temp = propertiesFile.getString(Skills[i] + "Destroy").split(",");
					for(String str : temp) {
						DestroySkill[Integer.parseInt(str)] = i;
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Create")) {
					temp = propertiesFile.getString(Skills[i] + "Create").split(",");
					for(String str : temp) {
						CreateSkill[Integer.parseInt(str)] = i;
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Tools")) {
					temp = propertiesFile.getString(Skills[i] + "Tools").split(",");
					for(String str : temp) {
						String[] temp2 = str.split("-");
						Tools[i][Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Weapons")) {
					temp = propertiesFile.getString(Skills[i] + "Weapons").split(",");
					for(String str : temp) {
						String[] temp2 = str.split("-");
						WeaponsSkill[Integer.parseInt(temp2[0])] = i;
						WeaponsDamage[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Armors")) {
					temp = propertiesFile.getString(Skills[i] + "Armors").split(",");
					for(String str : temp) {
						String[] temp2 = str.split("-");
						ArmorsSkill[Integer.parseInt(temp2[0])] = i;
						ArmorsDefense[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[2]);
						ArmorsDurability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Dodge")) {
					temp = propertiesFile.getString(Skills[i] + "Dodge").split(",");
					Dodge = new double[temp.length + 1];
					Dodge[0] = i;
					for(int ii = 0; ii < temp.length; ii++) {
						Dodge[ii+1] = Double.valueOf(temp[ii]);
					}
				}
			}
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

//	public void defaultConfig() {
//		try {
//			PropertiesFile props = new PropertiesFile("Skills.properties");
//			props = new PropertiesFile("Skills.properties");
//			props.load();
//			props.setBoolean("debug", true);
//			props.setBoolean("combat", true);
//			props.setInt("base-durability", 1);
//			props.setInt("to-broke", 5);
//			props.setInt("save-timer", 30000);
//			props.setDouble("weapon-mod", 0.3);
//			props.setDouble("armor-mod", 1);
//			props.save();
//		}
//		catch(Exception e) {}
//	}

	public static int GetDestroySkill(Block block) {
		return DestroySkill[block.getType()];
	}

	public static int GetToolLevel(int ItemInHand, int skill) {
		if(ItemInHand == -1)
			return 0;
		return Tools[skill][ItemInHand];
	}
	
	public static int GetDurability(Block block) {
		int durability = block.getData();
		if(durability == 0) {
			if(Durability[block.getType()] > 0) {
				durability = Durability[block.getType()];
			}
			else {
				durability = baseDurability;
			}
			etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
		}
		return durability;
	}

	public static int GetCreateSkill(Block block) {
		return CreateSkill[block.getType()];
	}

	public static int GetLevelFromExp(int expirence) {
		for(int i = Exp.length - 1; i > 0; i--) {
			if(expirence >= Exp[i])
				return i;
		}
		return 0;
	}

	public static String GetRangFromLevel(int level) {
		if(level < Rang.length)
			return Rang[level];
		else
			return "";
	}
}
