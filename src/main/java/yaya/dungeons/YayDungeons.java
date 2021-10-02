package yaya.dungeons;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import yaya.dungeons.dungeons.DungeonManager;
import yaya.dungeons.listeners.MobSpawnListener;
import yaya.dungeons.listeners.PortalListener;

import java.util.Objects;
import java.util.Random;

public final class YayDungeons extends JavaPlugin
{
	public static YayDungeons instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		try
		{
			getClass().getClassLoader().loadClass("yaya.dungeons.dungeons.DungeonManager");
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
		switch(command.getName())
		{
			case "dungen":
				Random r = new Random();
				DungeonManager.enterDungeon(p, DungeonManager.newDungeon(16, 16, r.nextInt()));
				return true;
			case "leave":
				DungeonManager.leaveDungeon(p);
				return true;
		}
		return false;
	}
}
