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
 * MaterialAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaterialAPI. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package me.cybermaxke.materialapi;

import java.util.logging.Level;

import me.cybermaxke.chunkdata.ChunkDataAPI;
import me.cybermaxke.materialapi.map.MapData;
import me.cybermaxke.materialapi.material.MaterialData;
import me.cybermaxke.materialapi.recipe.RecipeData;
import me.cybermaxke.materialapi.utils.Classes;
import me.cybermaxke.materialapi.utils.Metrics;

import org.bukkit.plugin.java.JavaPlugin;

public class MaterialAPI extends JavaPlugin {
	private String version = "v1.5.1";
	private static MaterialAPI instance;
	private static ChunkDataAPI chunkData;

	@Override
	public void onEnable() {
		instance = this;
		chunkData = new ChunkDataAPI(this);

		String pack = this.getServer().getClass().getPackage().getName();
   		this.version = pack.substring(pack.lastIndexOf('.') + 1).replace("_", ".");

   		new ArmorTask(this);
   		new PlayerListener(this);
		new Classes(this);
		new MaterialData(this);
		new MapData(this);
		new RecipeData();

		try {
			Metrics m = new Metrics(this);
			m.start();
			this.getLogger().log(Level.INFO, "Metrics loaded!");
		} catch (Exception e) {
			this.getLogger().log(Level.WARNING, "Couldn't load Metrics!");
		}

		this.getLogger().log(Level.INFO, "The api is loaded.");
	}

	@Override
	public void onDisable() {
		MaterialData.save();
		MapData.save();
	}

	/**
	public void loadConfig() {		
		File f = new File(this.getDataFolder(), "Config.yml");
		YamlConfiguration c = null;

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}

		if (!f.exists()) {
			c = new YamlConfiguration();

			try {
				f.createNewFile();
				c.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		c = YamlConfiguration.loadConfiguration(f);
	}
	*/

	public String getCraftbukkitPackage() {
		return "org.bukkit.craftbukkit." + (this.version.replace(".", "_"));
	}

	public String getNMSPackage() {
		return "net.minecraft.server." + (this.version.replace(".", "_"));
	}

	public static MaterialAPI getInstance() {
		return instance;
	}

	public static ChunkDataAPI getChunkData() {
		return chunkData;
	}
}