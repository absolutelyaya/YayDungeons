package yaya.dungeons;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.listeners.*;
import yaya.dungeons.menus.DungeonBrowserMenu;
import yaya.dungeons.utilities.DungeonManager;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

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
					DungeonManager.enterDungeon(p, DungeonManager.newDungeon(16, 16, random.nextInt()));
				} else if (args.length == 1)
				{
					DungeonManager.enterDungeon(p, DungeonManager.newDungeon(16, 16, Integer.parseInt(args[0])));
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
}
