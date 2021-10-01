package yaya.dungeons;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import yaya.dungeons.dungeons.DungeonManager;

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
		
		registerCommands();
	}
	
	@Override
	public void onDisable()
	{
		DungeonManager.removeAllDungeons();
	}
	
	public void registerCommands()
	{
		getCommand("dungen").setExecutor(this);
		getCommand("leave").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player)sender;
		switch(command.getName())
		{
			case "dungen":
				DungeonManager.enterDungeon(p, DungeonManager.newDungeon());
				return true;
			case "leave":
				DungeonManager.leaveDungeon(p);
				return true;
		}
		return false;
	}
}
