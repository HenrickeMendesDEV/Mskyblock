package mskyblock;

import java.util.LinkedHashMap;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import mskyblock.skyblock.Skyblock;

public class DataBase {
	public Main plugin;
	public Config messages, config;
	public LinkedHashMap<String, Object> skyblockDB, count;
	public static final int m_version = 1;
	
	public DataBase(Main plugin) {
		this.plugin = plugin;
		
		plugin.getDataFolder().mkdirs();
		initMessage();
		initDB();
		
		registerCommands();
		
		Skyblock.plugin = plugin;
		for (Object v1 : skyblockDB.values()) {
			String player = (String) ((LinkedHashMap<String, Object>)v1).get("player");
			LinkedHashMap<String, Object> shares = (LinkedHashMap<String, Object>) ((LinkedHashMap<String, Object>)v1).get("shares");
			int num = (int) ((LinkedHashMap<String, Object>)v1).get("num");
			Position spawn = (Position) ((LinkedHashMap<String, Object>)v1).get("spawn");
			Skyblock.skyblocklist.put(player, new Skyblock(player, shares, num, spawn));
		}
	}
	private static Position stringToPos(String str) {
		String[] args = str.split(":");
		return new Position(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Server.getInstance().getLevelByName(args[3]));
	}
	public void initMessage() {
		plugin.saveResource("messages.yml");
		messages = new Config(this.plugin.getDataFolder() + "/messages.yml", Config.YAML);
		updateMessage();
	}
	public void updateMessage() {
		if (messages.get("m_version", 1) < m_version) {
			this.plugin.saveResource("messages.yml", true);
			messages = new Config(this.plugin.getDataFolder() + "/messages.yml", Config.YAML);
		}
	}
	public void initDB() {
		skyblockDB = (LinkedHashMap<String, Object>) (new Config(plugin.getDataFolder() + "/skyblockDB.json", Config.JSON)).getAll();
		count = (LinkedHashMap<String, Object>) (new Config(plugin.getDataFolder() + "/count.json", Config.JSON, new LinkedHashMap<String, Object>() {
			{
				put("count", 0);
			}
		})).getAll();
	}
	public void save() {
		this.skyblockDB = Skyblock.toHashMap();
		Config skyblockDB = new Config(plugin.getDataFolder() + "/skyblockDB.json", Config.JSON);
		skyblockDB.setAll(this.skyblockDB);
		skyblockDB.save();
		
		Config count = new Config(plugin.getDataFolder() + "/count.json", Config.JSON);
		count.setAll(this.count);
		count.save();
	}
	public void registerCommands() {
		registerCommand(get("commands-skyblock"), get("commands-skyblock-description"), get("commands-skyblock-usage"), "mskyblock.commands.island");
	}
	public void registerCommand(String name, String description, String usage, String permission) {
		SimpleCommandMap commandMap = this.plugin.getServer().getCommandMap();
		PluginCommand<Main> command = new PluginCommand<Main>(name, plugin);
		command.setDescription(description);
		command.setUsage(usage);
		command.setPermission(permission);
		commandMap.register(name, command);
	}
	public String get(String key) {
		return this.messages.get(this.messages.get("default-language", "kor") + "-" + key, "default-value");
	}
	public void alert(CommandSender player, String message) {
		player.sendMessage(TextFormat.RED + get("default-prefix") + " " + message);
	}
	public void message(CommandSender player, String message) {
		player.sendMessage(TextFormat.DARK_AQUA + get("default-prefix") + " " + message);
	}
}