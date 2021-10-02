package yaya.dungeons.dungeons;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
				d.loadDungeonInventory(p);
			}
			else
				Dungeoneers.put(p, new Dungeoneer(id, p));
			p.getInventory().clear();
		}
	}
	
	public static void leaveDungeon(Player p)
	{
		if(Dungeoneers.containsKey(p))
		{
			Dungeon dungeon = Dungeons.get(Dungeoneers.get(p).getCurrentDungeon());
			dungeon.Leave(p);
			Dungeoneer d;
			dungeon.SaveDungeoneer(p, d = Dungeoneers.get(p));
			d.loadOutsideInventory(p);
			Dungeoneers.remove(p);
			p.sendMessage(ChatColor.GRAY + "You left the Dungeon.");
		}
		else
			p.sendMessage(ChatColor.RED + "You aren't in any Dungeon.");
	}
	
	public static void removeDungeon(UUID id)
	{
		Dungeon d = Dungeons.get(id);
		d.CloseDungeon();
	}
}
