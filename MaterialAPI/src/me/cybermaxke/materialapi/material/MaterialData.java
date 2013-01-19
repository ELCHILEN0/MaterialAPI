/**
 * 
 * This software is part of the MaterialAPI
 * 
 * This api allows plugin developers to create on a easy way custom
 * items with a custom id and recipes depending on them.
 * 
 * MaterialAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or 
 * any later version.
 *  
 * MerchantAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaterialAPI. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package me.cybermaxke.materialapi.material;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * Managing all the custom materials and ids.
 */
public class MaterialData {
	private static File dataFolder;
	private static File dataFile;
	
	public static String DATA_PREFIX = "MMCustomID: ";
	
	private static int data = 0;
	private static Map<Integer, CustomMaterial> byCustomId = new HashMap<Integer, CustomMaterial>();
	private static Map<String, CustomMaterial> byId = new HashMap<String, CustomMaterial>();
	private static Map<String, Integer> matData = new HashMap<String, Integer>();
	private static Map<Integer, String> matDataById = new HashMap<Integer, String>();

	public MaterialData(Plugin plugin) {
		dataFolder = plugin.getDataFolder();
		dataFile = new File(dataFolder + File.separator + "MaterialData.yml");
		load();
	}
	
	/**
	 * Saving all the data to the config file.
	 */
	public static void save() {
		YamlConfiguration c = new YamlConfiguration();
		
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
		
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			c = YamlConfiguration.loadConfiguration(dataFile);
		}
		
		for (Entry<String, Integer> d : matData.entrySet()) {
			c.set(d.getKey(), d.getValue());
		}
		
		try {
			c.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loading all the data from the config file.
	 */
	public static void load() {
		if (!dataFolder.exists() || !dataFile.exists()) {
			return;
		}
		
		YamlConfiguration c = YamlConfiguration.loadConfiguration(dataFile);

		for (Entry<String, Object> d : c.getValues(false).entrySet()) {
			matData.put(d.getKey(), (Integer) d.getValue());
			matDataById.put((Integer) d.getValue(), d.getKey());
		}
	}
	
	/**
	 * Returns the custom material from the given custom id.
	 * @param id The id.
	 * @return The material.
	 */
	public static CustomMaterial getMaterialByCustomId(int id) {
		return !byCustomId.containsKey(id) ? null : byCustomId.get(id);
	}
	
	/**
	 * Returns the custom material from the given id.
	 * @param id The id.
	 * @return The material.
	 */
	public static CustomMaterial getMaterialById(String id) {
		return !byId.containsKey(id.toLowerCase()) ? null : byId.get(id.toLowerCase());
	}
	
	/**
	 * Returns the next available id.
	 * @return
	 */
	public static int getNextId() {
		while (matDataById.containsKey(data)) {
			data++;
		}
		
		return data;
	}
	
	/**
	 * Registering the custom ids for material.
	 * @param material The material.
	 * @return The custom id.
	 */
	public static int addMaterialData(CustomMaterial material) {
		if (matData.containsKey(material.getId())) {
			return matData.get(material.getId());
		}
		
		int id = getNextId();
		
		matData.put(material.getId(), id);
		matDataById.put(id, material.getId());
		return id;
	}
	
	/**
	 * Registering the material to be able to use0
	 * @param material The material.
	 * @return The material.
	 */
	public static CustomMaterial addMaterial(CustomMaterial material) {
		byId.put(material.getId(), material);
		byCustomId.put(material.getCustomId(), material);
		return material;
	}
	
	/**
	 * Returns if the given itemstack is a custom item.
	 * @param itemstack The itemstack.
	 * @return If the itemstack is a custom item.
	 */
	public static boolean isCustomItem(ItemStack itemstack) {
		return getCustomId(itemstack) != -1;
	}
	
	/**
	 * Returns the custom id of the itemstack, '-1' if it's not a custom item.
	 * @param itemstack The itemstack.
	 * @return The custom id.
	 */
	public static int getCustomId(ItemStack itemstack) {
		ItemMeta m = itemstack.getItemMeta();
		
		if (m == null || !m.hasLore()) {
			return -1;
		}
		
		List<String> l = m.getLore();
		for (int i = 0; i < l.size(); i++) {
			String s = l.get(i);
			
			if (s.contains(DATA_PREFIX)) {
				return Integer.valueOf(s.replace(DATA_PREFIX, ""));
			}
		}
		
		return -1;
	}
}