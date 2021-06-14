package mk.plugin.lockeditems.main;

import java.io.File;

import mk.plugin.lockeditems.listener.TradeMeListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mk.plugin.lockeditems.command.LICommand;
import mk.plugin.lockeditems.listener.LIListener;
import mk.plugin.lockeditems.utils.LIUtils;

public class MainLockedItems extends JavaPlugin {
	
	public static boolean HAS_TRADEME = false;
	private static MainLockedItems main;
	private FileConfiguration config;

	public static MainLockedItems getMain() {
		return main;
	}

	public void onEnable() {
		main = this;
		this.saveDefaultConfig();
		this.reloadConfig();
		this.getCommand("lockeditems").setExecutor(new LICommand());
		Bukkit.getPluginManager().registerEvents(new LIListener(), this);
		if (Bukkit.getPluginManager().isPluginEnabled("TradeMe")) {
			HAS_TRADEME = true;
			Bukkit.getPluginManager().registerEvents(new TradeMeListener(), this);
		}

	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public void reloadConfig() {
		File file = new File(this.getDataFolder(), "config.yml");
		this.config = YamlConfiguration.loadConfiguration(file);

		LIUtils.UNLOCKED_LINE = config.getString("unlocked-lore").replace("&", "§");
		LIUtils.LOCK_FEE = config.getInt("lock-fee");
		LIUtils.BLOCKED_COMMANDS = config.getStringList("blocked-commands");
		
	}
}
