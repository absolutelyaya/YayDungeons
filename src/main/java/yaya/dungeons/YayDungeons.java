package yaya.dungeons;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import yaya.dungeons.listeners.*;
import yaya.dungeons.menus.DungeonBrowserMenu;
import yaya.dungeons.utilities.DungeonManager;

import java.util.Objects;
import java.util.Random;

public final class YayDungeons extends JavaPlugin
{
	public static YayDungeons instance;
	Random random;
	
	@Override
	public void onEnable()
	{
		instance = this;
		random = new Random();
		
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
					DungeonManager.enterDungeon(p, DungeonManager.newDungeon(16, 16, random.nextInt()));
				} else if (args.length == 1)
				{
					DungeonManager.enterDungeon(p, DungeonManager.newDungeon(16, 16, Integer.parseInt(args[0])));
				}
				return true;
			}
			case "leave" -> {
				DungeonManager.leaveDungeon(p);
				return true;
			}
			case "dungeons" -> {
				new DungeonBrowserMenu(p, p).open();
				return true;
			}
		}
		return false;
	}
}
