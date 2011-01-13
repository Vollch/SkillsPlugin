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
	public static boolean explosionOn;
	public static double explosionDrop;
	public static double weaponMod;
	public static double armorMod;
	public static double monsterMod;

	public static boolean loadConfig() {
		try {
			String[] temp;
			String[] temp2;
			propertiesFile.load();
			debugOn = propertiesFile.getBoolean("debugOn", false);
			combatOn = propertiesFile.getBoolean("combatOn", true);
			explosionOn = propertiesFile.getBoolean("explosionOn", true);
			toBroke = propertiesFile.getInt("toBroke", 5);
			saveTimer = propertiesFile.getInt("saveTimer", 30000);
			weaponMod = propertiesFile.getDouble("weaponMod", 0.3);
			armorMod = propertiesFile.getDouble("armorMod", 1);
			monsterMod = propertiesFile.getDouble("monsterMod", 5);
			explosionDrop = propertiesFile.getDouble("explosionDrop", 1);
			BaseDurability = propertiesFile.getInt("BaseDurability", 1);
			
			if(propertiesFile.containsKey("Durability")){
				temp = propertiesFile.getString("Durability").split(",");
				for(String str : temp) {
				temp2 = str.split("-");
				Durability[Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
				}
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
				if(propertiesFile.containsKey(Skills[i] + "Armor")) {
					temp = propertiesFile.getString(Skills[i] + "Armor").split(",");
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

	public static boolean defaultConfig() {
		try {
			propertiesFile.load();
			propertiesFile.setString("debugOn", "false");
			propertiesFile.setString("combatOn", "true");
			propertiesFile.setString("explosionOn", "true");
			propertiesFile.setString("toBroke", "5");
			propertiesFile.setString("saveTimer", "30000");
			propertiesFile.setString("weaponMod", "0.3");
			propertiesFile.setString("armorMod", "1");
			propertiesFile.setString("monsterMod", "5");
			propertiesFile.setString("explosionDrop", "1");
			propertiesFile.setString("BaseDurability", "1");
			propertiesFile.setString("Durability", "1-2,2-2,3-2,4-2,5-2,12-2,13-4,14-7,15-3,16-2,17-2,41-2,42-7,43-3,44-3,45-6,47-3,48-6,49-10,56-13,57-15,73-6,74-6,78-2,80-3,82-3,87-10,88-10,89-10,91-9");
			propertiesFile.setString("SkillNames", "Miner,Mason,Woodcutter,Carpenter,Gemcutter,Metalcrafter,Hauler,Wrestler,Axeman,Swordsman,Bowman,Armoruser,Dodger");
			propertiesFile.setString("BaseExp", "0,200,450,750,1100,1500,1950,2450,3000,3600,4250,4950,5700,6500,7350");
			propertiesFile.setString("BaseRang", "BaseRang=Novice,Adequate,Competent,Skilled,Proficient,Talented,Adept,Expert,Professional,Accomplished,Great,Master,High Master,Grand Master,Legendary");
			propertiesFile.setString("MinerDestroy", "1,4,43,44,45,48,49,67,87,89");
			propertiesFile.setString("MinerTools", "278-3,257-2,274-1,270-1,285-1");
			propertiesFile.setString("MinerExp", "0,200,600,1400,2400,3600,4800,6400,8200,10200,12400,14800,17400,20200,23200");
			propertiesFile.setString("MinerRang", "Novice,Adequate,Competent,Skilled,Proficient,Talented,Adept,Expert,Professional,Accomplished,Great,Master,High Master,Grand Master,Crazy");
			propertiesFile.setString("MasonCreate", "1,4,43,44,45,48,49,67,87,89");
			propertiesFile.setString("WoodcutterDestroy", "5,17,47,53,85");
			propertiesFile.setString("WoodcutterTools", "279-3,258-2,275-1,271-1,286-1");
			propertiesFile.setString("CarpenterCreate", "5,17,47,53,85");
			propertiesFile.setString("GemcutterDestroy", "14,15,16,20,41,42,56,57,73,74");
			propertiesFile.setString("GemcutterCreate", "20");
			propertiesFile.setString("GemcutterTools", "278-3,257-2,274-1,270-1,285-1");
			propertiesFile.setString("MetalcrafterCreate", "41,42,57");
			propertiesFile.setString("HaulerDestroy", "2,3,12,13,35,60,78,80,82,88");
			propertiesFile.setString("HaulerCreate", "2,3,12,13,35,60,78,80,82,88");
			propertiesFile.setString("HaulerTools", "277-3,256-2,273-1,269-1,284-1");
			propertiesFile.setString("HaulerExp", "0,300,700,1200,1800,2500,3300,4200,5200,6300,7500,8800,10200,11700,13300");
			propertiesFile.setString("WrestlerWeapon", "399-2");
			propertiesFile.setString("AxemanWeapon", "271-1,286-2,275-3,258-4,279-6");
			propertiesFile.setString("SwordsmanWeapon", "268-2,283-3,272-4,267-5,276-7");
			propertiesFile.setString("BowmanWeapon", "261-2");
			propertiesFile.setString("ArmoruserArmor", "298-34-3,299-48-3,300-46-3,301-40-3,302-67-4,303-96-4,304-92-4,305-79-4,306-136-5,307-192-5,308-184-5,309-160-5,310-272-7,311-384-7,312-368-7,313-320-7,314-68-4,315-96-4,316-92-4,317-80-4");
			propertiesFile.setString("DodgerDodge", "0.02,0.04,0.06,0.08,0.10,0.12,0.14,0.16,0.18,0.20,0.22,0.24,0.26,0.28,0.30");
			propertiesFile.setString("DodgerExp", "0,30,120,300,570,950,1440,2050,2810,3720,4800,6050,7480,9120,10970");
			propertiesFile.save();
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
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
		if(getDestroySkill(block.getType()) == 0){
			return 1;
		}
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
