
public class SkillsProperties {
	private static final PropertiesFile propertiesFile = new PropertiesFile("Skills.properties");
	private static int BaseDurability;
	private static int[] Durability = new int[100];
	private static int[] DestroySkill = new int[100];
	private static int[] CreateSkill = new int[100];
	private static int[] WeaponSkill = new int[400]; 
	private static int[] ArmorSkill = new int[400];
	private static int[] ArmorDurability = new int[400];
	private static int[][] SkillItem;
	private static int[] BaseExp;
	private static int[][] SkillExp;
	private static String[] BaseRang;
	private static String[][] SkillRang;
	
	public static double[] Dodge;
	public static String[] Skills;
	public static int toBroke;
	public static int saveTimer;
	public static boolean debugOn;
	public static boolean combatOn;
	public static double weaponMod;
	public static double armorMod;

	public static boolean loadConfig() {
		try {
			String[] temp;
			String[] temp2;
			propertiesFile.load();
			debugOn = propertiesFile.getBoolean("debugOn");
			combatOn = propertiesFile.getBoolean("combatOn");
			toBroke = propertiesFile.getInt("toBroke");
			saveTimer = propertiesFile.getInt("saveTimer");
			weaponMod = propertiesFile.getDouble("weaponMod");
			armorMod = propertiesFile.getDouble("armorMod");
			BaseDurability = propertiesFile.getInt("BaseDurability");
			
			if(!propertiesFile.containsKey("Durability"))
				return false;
			temp = propertiesFile.getString("Durability").split(",");
			for(String str : temp) {
				temp2 = str.split("-");
				Durability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
			}
		
			if(!propertiesFile.containsKey("BaseExp"))
				return false;
			temp = propertiesFile.getString("BaseExp").split(",");
			BaseExp = new int[16];
			for(int i = 0; i < 15; i++) {
				BaseExp[i + 1] = Integer.parseInt(temp[i]);
			}
			
			if(!propertiesFile.containsKey("BaseRang"))
				return false;
			temp = propertiesFile.getString("BaseRang").split(",");
			BaseRang = new String[16];
			for(int i = 0; i < 15; i++) {
				BaseRang[i + 1] = temp[i];
			}
			
			if(!propertiesFile.containsKey("SkillNames"))
				return false;
			temp = propertiesFile.getString("SkillNames").split(",");
			Skills = new String[temp.length + 1];
			for(int i = 0; i < temp.length; i++) {
				Skills[i+1] = temp[i];
			}
	
			
			SkillExp = new int[Skills.length][16];
			SkillRang = new String[Skills.length][16];
			SkillItem = new int[Skills.length][400];
			
			for(int i = 1; i < Skills.length; i++) {
				if(propertiesFile.containsKey(Skills[i] + "Exp")) {
					temp = propertiesFile.getString(Skills[i] + "Exp").split(",");
					for(int ii = 1; ii <= 15; ii++){
						SkillExp[i][ii] = Integer.parseInt(temp[ii-1]);
					}

				}
				else {
					for(int ii = 1; ii <= 15; ii++){
						SkillExp[i][ii] = BaseExp[ii];
					}
				}
				
				if(propertiesFile.containsKey(Skills[i] + "Rang")) {
					temp = propertiesFile.getString(Skills[i] + "Rang").split(",");
					for(int ii = 1; ii <= 15; ii++){
						SkillRang[i][ii] = temp[ii-1];
					}

				}
				else {
					for(int ii = 1; ii <= 15; ii++){
						SkillRang[i][ii] = BaseRang[ii];
					}
				}

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
						temp2 = str.split("-");
						SkillItem[i][Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Weapon")) {
					temp = propertiesFile.getString(Skills[i] + "Weapon").split(",");
					for(String str : temp) {
						temp2 = str.split("-");
						SkillItem[i][Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
						WeaponSkill[Integer.parseInt(temp2[0])] = i;
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Armors")) {
					temp = propertiesFile.getString(Skills[i] + "Armors").split(",");
					for(String str : temp) {
						temp2 = str.split("-");
						SkillItem[i][Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[2]);
						ArmorSkill[Integer.parseInt(temp2[0])] = i;
						ArmorDurability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
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
//  TODO: Nuff said.
	
	public static int getDestroySkill(int block) {
		return DestroySkill[block];
	}
	
	public static int getCreateSkill(int block) {
		return CreateSkill[block];
	}
	
	public static int getWeaponSkill(int item) {
		if(item == -1)
			item = 399;
		return WeaponSkill[item];
	}

	public static int getArmorSkill(int item) {
		if(item == -1)
			item = 399;
		return ArmorSkill[item];
	}
	
	public static int getItemLevel(int item, int skill) {
		if(item == -1)
			item = 399;
		return SkillItem[skill][item];
	}
	
	public static int getBlockDurability(Block block) {
		int durability = block.getData();
		if(durability == 0) {
			if(Durability[block.getType()] > 0) {
				durability = Durability[block.getType()];
			}
			else {
				durability = BaseDurability;
			}
			etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), durability);
		}
		return durability;
	}
	
	public static int getArmorDurability(int item) {
		return ArmorDurability[item];
	}

	public static int getLevelFromExp(int expirence, int skill) {
		for(int i = 15; i > 0; i--) {
			if(expirence >= SkillExp[skill][i])
				return i;
		}
		return 0;
	}
	
	public static int getExpForLevel(int level, int skill) {
		return SkillExp[skill][level];
	}	

	public static String getRangForLevel(int level, int skill) {
		return SkillRang[skill][level];
	}
	
	public static String getSkillName(int skill) {
		return Skills[skill];
	}
}
