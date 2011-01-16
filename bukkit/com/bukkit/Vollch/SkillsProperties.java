package com.bukkit.Vollch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import org.bukkit.block.Block;

public class SkillsProperties {
	private static final PropertiesFile propertiesFile = new PropertiesFile("Skills.properties");
	
	private static HashMap<Integer, Byte> customBlocks = new HashMap<Integer, Byte>();

	private static int[] Durability = new int[100];
	private static boolean[] Custom = new boolean[100];
	private static int[] DestroySkill = new int[100];
	private static int[] CreateSkill = new int[100];
	private static int[] GatherSkill = new int[100];
	private static HashMap<Integer, Hashtable<Integer, Double>> gatherItems = new HashMap<Integer, Hashtable<Integer, Double>>();
	private static int[][] SkillItem;
	private static int[] BaseExp;
	private static int[][] SkillExp;
	private static String[] BaseRang;
	private static String[][] SkillRang;
	
	public static String[] Skills;

	public static boolean debugOn;
	public static boolean levelDependentCreatyGain;
	public static boolean levelDependentDestroyGain;
	public static boolean levelDependentGatherGain;
	public static int toBroke;
	public static int saveTimer;
	
	public static boolean loadConfig(){
		try {
			loadBlocks();
			String[] temp;
			String[] temp2;
			propertiesFile.load();
			debugOn = propertiesFile.getBoolean("debugOn", false);
			levelDependentCreatyGain = propertiesFile.getBoolean("levelDependentCreatyGain", false);
			levelDependentDestroyGain = propertiesFile.getBoolean("levelDependentDestroyGain", false);
			levelDependentGatherGain = propertiesFile.getBoolean("levelDependentGatherGain", false);
			toBroke = propertiesFile.getInt("toBroke", 5);
			saveTimer = propertiesFile.getInt("saveTimer", 60000);
		
			if(propertiesFile.containsKey("Custom")){
				temp = propertiesFile.getString("Custom").split(",");
				for(String s : temp){
					Custom[Integer.valueOf(s)] = true;
				}
			}
			
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
				if(propertiesFile.containsKey(Skills[i] + "Gather")) {
					temp = propertiesFile.getString(Skills[i] + "Gather").split(",");
					for(String str : temp) {
						temp2 = str.split("-");
						GatherSkill[Integer.parseInt(temp2[0])] = i;
						Hashtable<Integer, Double> table = new Hashtable<Integer, Double>();
						for(int ii = 1; ii < temp2.length; ii+=2){
							table.put(Integer.valueOf(temp2[ii]), Double.valueOf(temp2[ii+1]));
						}
						gatherItems.put(Integer.parseInt(temp2[0]), table);
					}
				}
				if(propertiesFile.containsKey(Skills[i] + "Tools")) {
					temp = propertiesFile.getString(Skills[i] + "Tools").split(",");
					for(String str : temp) {
						temp2 = str.split("-");
						SkillItem[i][Integer.parseInt(temp2[0])] = Integer.parseInt(temp2[1]);
					}
				}
			}
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean defaultConfig() {
		try {
			propertiesFile.load();
			propertiesFile.setString("debugOn", "false");
			propertiesFile.setString("levelDependentCreateGain", "false");
			propertiesFile.setString("levelDependentDestroyGain", "false");
			propertiesFile.setString("levelDependentGatherGain", "false");
			propertiesFile.setString("toBroke", "3");
			propertiesFile.setString("saveTimer", "60000");
			propertiesFile.setString("Custom", "17,26,27,28,29,30,31,32,33,34,35,35");
			propertiesFile.setString("Durability", "1-2,2-2,3-2,4-2,5-2,7-15,12-2,13-4,14-7,15-3,16-2,17-2,21-6,22-8,24-2,41-2,42-7,43-3,44-3,45-6,47-3,48-6,49-10,56-13,57-15,73-6,74-6,78-2,80-3,82-3,87-10,88-10,89-10");
			propertiesFile.setString("SkillNames", "Miner,Mason,Woodcutter,Carpenter,Gemcutter,Metalcrafter,Hauler,Clother,Harvester");
			propertiesFile.setString("BaseExp", "0,200,450,750,1100,1500,1950,2450,3000,3600,4250,4950,5700,6500,7350");
			propertiesFile.setString("BaseRang", "Novice,Adequate,Competent,Skilled,Proficient,Talented,Adept,Expert,Professional,Accomplished,Great,Master,High Master,Grand Master,Legendary");
			propertiesFile.setString("MinerDestroy", "1,4,7,24,43,44,45,48,49,67,87,89");
			propertiesFile.setString("MinerTools", "278-3,257-2,274-1,270-1,285-1");
			propertiesFile.setString("MinerExp", "0,200,600,1400,2400,3600,4800,6400,8200,10200,12400,14800,17400,20200,23200");
			propertiesFile.setString("MinerRang", "Novice,Adequate,Competent,Skilled,Proficient,Talented,Adept,Expert,Professional,Accomplished,Great,Master,High Master,Grand Master,Crazy");
			propertiesFile.setString("MasonCreate", "1,4,7,24,43,44,45,48,49,67,87,89");
			propertiesFile.setString("WoodcutterDestroy", "5,17,47,53,85");
			propertiesFile.setString("WoodcutterTools", "279-3,258-2,275-1,271-1,286-1");
			propertiesFile.setString("CarpenterCreate", "5,17,47,53,85");
			propertiesFile.setString("GemcutterDestroy", "14,15,16,20,21,41,42,56,57,73,74");
			propertiesFile.setString("GemcutterCreate", "20");
			propertiesFile.setString("GemcutterTools", "278-3,257-2,274-1,270-1,285-1");
			propertiesFile.setString("MetalcrafterCreate", "22,41,42,57");
			propertiesFile.setString("HaulerDestroy", "2,3,12,13,60,78,80,82,88");
			propertiesFile.setString("HaulerCreate", "2,3,12,13,60,78,80,82,88");
			propertiesFile.setString("HaulerTools", "277-3,256-2,273-1,269-1,284-1");
			propertiesFile.setString("HaulerExp", "0,300,700,1200,1800,2500,3300,4200,5200,6300,7500,8800,10200,11700,13300");
			propertiesFile.setString("ClotherDestroy", "26,27,28,29,30,31,32,33,34,35,36");
			propertiesFile.setString("ClotherCreate", "26,27,28,29,30,31,32,33,34,35,36");
			propertiesFile.setString("HarvesterGather", "18-5-0.03-18-0.03-260-0.01-323-0.003,37-37-0.2,38-38-0.2,39-39-0.2,40-40-0.2,59-296-0.3-295-0.1");
			propertiesFile.save();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getDestroySkill(int block) {
		return DestroySkill[block];
	}
	
	public static int getGatherSkill(int block) {
		return GatherSkill[block];
	}
	
	public static int getCreateSkill(int block) {
		return CreateSkill[block];
	}
	
	public static int getItemLevel(int item, int skill) {
		if(item == -1)
			item = 399;
		return SkillItem[skill][item];
	}
	
	public static int getBlockDurability(Block block) {
		if(Durability[block.getTypeId()] == 0)
			return 1;	
		else
		{	
			if(Custom[block.getTypeId()]){
				if(customBlocks.containsKey(getBlockHash(block)))
					return customBlocks.get(getBlockHash(block));	
				else
					return Durability[block.getTypeId()];
			}
			else
			{
				int durability = block.getData();
				if(durability == 0) {
					if(Durability[block.getTypeId()] > 0) {
						durability = Durability[block.getTypeId()];
					}
					else {
						durability = 1;
					}
					block.setData((byte)durability);
				}
				return durability;
			}
		}
	}
	
	public static void setBlockDurability(Block block, int durability) {
		if(Custom[block.getTypeId()]){
			int hash = getBlockHash(block);
			if(durability < 1 && customBlocks.containsKey(hash)){
				customBlocks.remove(hash);
			}
			else{
				customBlocks.put(hash, (byte)durability);
			}
		}
		else{
			block.setData((byte)durability);
		}
	}

	public static boolean saveDurability() {
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SkillsBlocks.save"));
			out.writeObject(customBlocks);
			out.close();
			return true;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean loadBlocks(){
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("SkillsBlocks.save"));
			customBlocks = (HashMap<Integer, Byte>)in.readObject();
			in.close();
			return true;
		} 
		catch (Exception e)
		{
			//1st run, exception is normal.
			//e.printStackTrace();
			return false;
		}
	}
	
	
    public static int getBlockHash(Block block) {
        int hash = 7;
        hash = 97 * hash + block.getX();
        hash = 97 * hash + block.getY();
        hash = 97 * hash + block.getZ();
        return hash;
    }
	
	public static Integer[] getDrop(Block block, int level){
		ArrayList<Integer> items = new ArrayList<Integer>();
		for(int i = 0; i < level; i++){
			Hashtable<Integer, Double> table = gatherItems.get(block.getTypeId());
			Enumeration<Integer> keys = table.keys();
			while(keys.hasMoreElements()) {
				int item = keys.nextElement();
				if(Math.random() < table.get(item)){
					items.add(item);
				}
			}
		}
		Integer[] ret = new Integer[items.size()];
		items.toArray(ret);
		return ret;
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
