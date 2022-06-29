package yaya.dungeons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.dungeons.DungeonSize;
import yaya.dungeons.listeners.*;
import yaya.dungeons.menus.DungeonBrowserMenu;
import yaya.dungeons.utilities.DungeonManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class YayDungeons extends JavaPlugin
{
	public static YayDungeons instance;
	
	public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public ConfigData dungeonConfig;
	
	File dungeonConfigFile = new File(getDataFolder(), "dungeonConfig.json");
	Random random;
	
	@Override
	public void onEnable()
	{
		instance = this;
		random = new Random();
		
		if(!dungeonConfigFile.exists())
		{
			dungeonConfigFile.getParentFile().mkdirs();
			saveResource(dungeonConfigFile.getName(), false);
		}
		loadConfig();
		
		try
		{
			getClass().getClassLoader().loadClass("yaya.dungeons.utilities.DungeonManager");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		registerListeners();
		registerCommands();
	}
	
	private void loadConfig()
	{
		try
		{
			dungeonConfig = gson.fromJson(new FileReader(dungeonConfigFile), ConfigData.class);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable()
	{
		DungeonManager.removeAllDungeons();
	}
	
	public void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new PortalListener(), this);
		getServer().getPluginManager().registerEvents(new MobSpawnListener(), this);
		getServer().getPluginManager().registerEvents(new MenuListener(), this);
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
		getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
	}
	
	public void registerCommands()
	{
		Objects.requireNonNull(getCommand("dungen")).setExecutor(this);
		Objects.requireNonNull(getCommand("leave")).setExecutor(this);
		Objects.requireNonNull(getCommand("dungeonaccept")).setExecutor(this);
		Objects.requireNonNull(getCommand("dungeondecline")).setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player)sender;
		switch (command.getName())
		{
			case "dungen" -> {
				if (args.length == 0)
				{
					DungeonManager.enterDungeon(p, DungeonManager.newDungeon(random.nextInt()));
				} else if (args.length == 1)
				{
					DungeonManager.enterDungeon(p, DungeonManager.newDungeon(Integer.parseInt(args[0])));
				}
				return true;
			}
			case "leave" -> {
				DungeonManager.leaveDungeon(p, 1);
				return true;
			}
			case "dungeons" -> {
				new DungeonBrowserMenu(p, p).open();
				return true;
			}
			case "dungeonmods" -> {
				if(DungeonManager.isWorldDungeon(p.getWorld()))
				{
					DungeonManager.getDungeon(UUID.fromString(p.getWorld().getName().split("\\.")[1])).DisplayModsSimple(p);
					return true;
				}
				else
					p.sendMessage(Component.text("You aren't in a Dungeon!"));
				return false;
			}
			case "dungeonaccept" -> {
				if(DungeonManager.isWorldDungeon(p.getWorld()) && args.length == 1)
				{
					UUID dungeonID = UUID.fromString(p.getWorld().getName().split("\\.")[1]);
					Dungeon d = DungeonManager.getDungeon(dungeonID);
					if(d.getLeader().equals(p))
					{
						Player target = Bukkit.getPlayer(args[0]);
						if (!DungeonManager.AcceptJoinRequest(target, dungeonID))
							p.sendMessage(Component.text(ChatColor.RED + "Request invalid."));
						return true;
					}
				}
				return false;
			}
			case "dungeondecline" -> {
				if(DungeonManager.isWorldDungeon(p.getWorld()) && args.length == 1)
				{
					UUID dungeonID = UUID.fromString(p.getWorld().getName().split("\\.")[1]);
					Dungeon d = DungeonManager.getDungeon(dungeonID);
					if(d.getLeader() == p)
					{
						Player target = Bukkit.getPlayer(args[0]);
						if (!DungeonManager.DeclineJoinRequest(target, dungeonID))
							p.sendMessage(Component.text(ChatColor.RED + "Request invalid."));
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	public static class ConfigData
	{
		public Map<String, DungeonSize> Sizes;
		public Map<String, Boolean> Modifiers;
		
		public List<Dungeon.Modifier> getDisabledMods()
		{
			List<Dungeon.Modifier> result = new ArrayList<>();
			for (String key : Modifiers.keySet())
			{
				if(!Modifiers.get(key))
					result.add(Enum.valueOf(Dungeon.Modifier.class, key));
			}
			return result;
		}
	}
}
