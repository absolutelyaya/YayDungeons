package yaya.dungeons.utilities;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.dungeons.Dungeoneer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DungeonManager
{
	static HashMap<UUID, Dungeon> Dungeons = new HashMap<>();
	static HashMap<Player, Dungeoneer> Dungeoneers = new HashMap<>();
	
	public static UUID newDungeon(int sizeX, int sizeY, int seed)
	{
		UUID id = UUID.randomUUID();
		Dungeon d = new Dungeon(id, sizeX, sizeY, seed);
		Dungeons.put(id, d);
		return id;
	}
	
	public static Dungeon getDungeon(UUID id)
	{
		return Dungeons.get(id);
	}
	
	public static List<Dungeon> getDungeons()
	{
		return Dungeons.values().stream().toList();
	}
	
	public static boolean isWorldDungeon(World world)
	{
		for (UUID id : Dungeons.keySet())
		{
			if((Dungeons.get(id)).getWorld().equals(world))
			{
				return true;
			}
		}
		return false;
	}
	
	public static void removeAllDungeons()
	{
		for (UUID id : Dungeons.keySet())
		{
			removeDungeon(id);
		}
		Dungeons.clear();
	}
	
	public static void enterDungeon(Player p, UUID id)
	{
		if(Dungeoneers.containsKey(p))
		{
			leaveDungeon(p);
		}
		if(Dungeons.containsKey(id))
		{
			Dungeon dungeon = Dungeons.get(id);
			dungeon.Enter(p);
			if(dungeon.getSavedDungeoneers().containsKey(p))
			{
				Dungeoneer d;
				Dungeoneers.put(p, (d = dungeon.getSavedDungeoneers().get(p)));
				d.setCurrentDungeon(id);
			}
			else
			{
				Dungeoneers.put(p, new Dungeoneer(id, p));
			}
			p.setGameMode(GameMode.ADVENTURE);
		}
	}
	
	public static void leaveDungeon(Player p)
	{
		if(Dungeoneers.containsKey(p))
		{
			Dungeon dungeon = Dungeons.get(Dungeoneers.get(p).getCurrentDungeon());
			dungeon.Leave(p);
			dungeon.SaveDungeoneer(p, Dungeoneers.get(p));
			Dungeoneers.remove(p);
			p.setGameMode(GameMode.SURVIVAL);
			p.sendMessage(ChatColor.GRAY + "You left the Dungeon.");
		}
		else
			p.sendMessage(ChatColor.RED + "You aren't in any Dungeon.");
	}
	
	public static Dungeoneer getDungeoneer(Player p)
	{
		return Dungeoneers.get(p);
	}
	
	public static void removeDungeon(UUID id)
	{
		Dungeon d = Dungeons.get(id);
		d.CloseDungeon();
		Dungeons.remove(id);
	}
}
