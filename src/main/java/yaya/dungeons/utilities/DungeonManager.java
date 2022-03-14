package yaya.dungeons.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.dungeons.Dungeoneer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DungeonManager
{
	static HashMap<UUID, Dungeon> Dungeons = new HashMap<>();
	static HashMap<Player, Dungeoneer> Dungeoneers = new HashMap<>();
	static HashMap<Player, UUID> pendingJoinRequests = new HashMap<>();
	
	public static UUID newDungeon(int seed)
	{
		UUID id = UUID.randomUUID();
		Dungeon d = new Dungeon(id, seed);
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
			if (!leaveDungeon(p, 1))
				return;
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
	
	public static boolean leaveDungeon(Player p, int method)
	{
		if(Dungeoneers.containsKey(p))
		{
			Dungeon dungeon = Dungeons.get(Dungeoneers.get(p).getCurrentDungeon());
			boolean result = dungeon.Leave(p, method);
			if (result)
			{
				dungeon.SaveDungeoneer(p, Dungeoneers.get(p));
				Dungeoneers.remove(p);
				p.setGameMode(GameMode.SURVIVAL);
				p.sendMessage(ChatColor.GRAY + "You left the Dungeon.");
			}
			return result;
		}
		else
			p.sendMessage(ChatColor.RED + "You aren't in any Dungeon.");
		return false;
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
	
	public static void sendJoinRequest(Player p, UUID dungeonID)
	{
		if(isWorldDungeon(p.getWorld()))
		{
			p.sendMessage(Component.text(ChatColor.RED + "You're already in a Dungeon!"));
			return;
		}
		if(!getDungeon(dungeonID).isCanJoin(p))
		{
			p.sendMessage(Component.text(ChatColor.RED + "You can't join this dungeon!"));
			return;
		}
		pendingJoinRequests.put(p, dungeonID);
		getDungeon(dungeonID).getLeader().sendMessage(
				Component.text(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " would like to join this Dungeon.")
						.append(Component.text(ChatColor.GREEN + " [Accept]").clickEvent(ClickEvent.runCommand("/dungeonaccept " + p.getName())))
						.append(Component.text(ChatColor.RED + " [Decline]").clickEvent(ClickEvent.runCommand("/dungeondecline " + p.getName()))));
		p.sendMessage(Component.text(ChatColor.YELLOW + "Join Request sent."));
		p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
	}
	
	public static boolean AcceptJoinRequest(Player p, UUID dungeonID)
	{
		if(pendingJoinRequests.containsKey(p) && pendingJoinRequests.get(p).equals(dungeonID))
		{
			Bukkit.broadcast(Component.text("a"));
			enterDungeon(p, dungeonID);
			pendingJoinRequests.remove(p);
			return true;
		}
		return false;
	}
	
	public static boolean DeclineJoinRequest(Player p, UUID dungeonID)
	{
		if(pendingJoinRequests.containsKey(p) && pendingJoinRequests.get(p).equals(dungeonID))
		{
			pendingJoinRequests.remove(p);
			return true;
		}
		return false;
	}
}
