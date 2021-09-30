package yaya.dungeons.dungeons;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dungeon
{
	private UUID id;
	private int sizeX;
	private int sizeY;
	
	World world;
	List<Player> players = new ArrayList<>();
	List<Player> spectators = new ArrayList<>();
	
	public Dungeon(UUID id, int sizeX, int sizeY)
	{
		this.id = id;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		generate();
	}
	
	void generate()
	{
		Bukkit.broadcastMessage("Generating a dungeon...");
		WorldCreator wc = new WorldCreator("Dungeon-" + id.toString());
		wc.type(WorldType.FLAT);
		wc.generatorSettings("{\"structures\": {\"structures\": {\"village\": {\"salt\": 8015723, \"spacing\": 32, \"separation\": 8}}}, " +
									 "\"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\": \"the_void\"}");
		wc.generateStructures(false);
		world = wc.createWorld();
		Bukkit.broadcastMessage("Dungeon Generation Complete!");
		
		world.getBlockAt(0, 63, 0).setType(Material.GLASS);
	}
	
	public void CloseDungeon()
	{
		List<Player> allPlayers = world.getPlayers();
		allPlayers.addAll(spectators);
		for (Player p : allPlayers)
		{
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			p.sendMessage(ChatColor.RED + "The Dungeon instance you were in was closed.");
		}
		Bukkit.unloadWorld(world, false);
		deleteRecursively(world.getWorldFolder());
	}
	
	public void Enter(Player p)
	{
		players.add(p);
		p.teleport(new Location(world, 0.5, 64, 0.5));
		p.sendMessage(ChatColor.GRAY + "You entered the Dungeon.");
	}
	
	public void Leave(Player p)
	{
		players.remove(p);
		Location l = p.getBedSpawnLocation();
		if(l != null)
			p.teleport(l);
		else
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		if(players.size() == 0)
			CloseDungeon();
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public static void deleteRecursively(File directory)
	{
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
			{
				deleteRecursively(file);
			} else
			{
				file.delete();
			}
		}
		directory.delete();
	}
}
