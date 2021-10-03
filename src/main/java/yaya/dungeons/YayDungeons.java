package yaya.dungeons;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import yaya.dungeons.listeners.DeathListener;
import yaya.dungeons.utilities.DungeonManager;
import yaya.dungeons.dungeons.Dungeoneer;
import yaya.dungeons.listeners.MenuListener;
import yaya.dungeons.listeners.MobSpawnListener;
import yaya.dungeons.listeners.PortalListener;
import yaya.dungeons.menus.ClassSelectionMenu;

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
	}
	
	public void registerCommands()
	{
		Objects.requireNonNull(getCommand("dungen")).setExecutor(this);
		Objects.requireNonNull(getCommand("leave")).setExecutor(this);
		Objects.requireNonNull(getCommand("class")).setExecutor(this);
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
			case "class":
				Dungeoneer d;
				if((d = DungeonManager.getDungeoneer(p)) != null)
				{
					if(d.getProfession().equals(Dungeoneer.Profession.Rookie))
					{
						new ClassSelectionMenu(p, p).open();
					}
				}
				break;
		}
		return false;
	}
}
