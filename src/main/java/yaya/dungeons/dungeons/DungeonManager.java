package yaya.dungeons.dungeons;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DungeonManager
{
	static HashMap<UUID, Dungeon> Dungeons = new HashMap<>();
	static HashMap<Player, UUID> Dungeoneers = new HashMap<>();
	
	public static UUID newDungeon()
	{
		UUID id = UUID.randomUUID();
		Dungeon d = new Dungeon(id, 16, 16);
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
	
	public static void enterDungeon(Player p, UUID dungeon)
	{
		if(Dungeons.containsKey(dungeon))
		{
			Dungeons.get(dungeon).Enter(p);
			Dungeoneers.put(p, dungeon);
		}
	}
	
	public static void leaveDungeon(Player p)
	{
		if(Dungeoneers.containsKey(p))
		{
			Dungeons.get(Dungeoneers.get(p)).Leave(p);
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
