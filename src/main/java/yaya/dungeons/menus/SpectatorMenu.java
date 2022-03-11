package yaya.dungeons.menus;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.utilities.ItemFactory;

import java.util.List;

public class SpectatorMenu extends Menu
{
	final Dungeon dungeon;
	List<Player> players;
	
	public SpectatorMenu(Player owner, Player virtualPlayer, Dungeon d)
	{
		super(owner, virtualPlayer);
		this.dungeon = d;
	}
	
	@Override
	public String getName()
	{
		return "Spectator Menu";
	}
	
	@Override
	public int getSlots()
	{
		return 27;
	}
	
	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		int slot = e.getSlot();
		if(slot == 22)
			dungeon.StopSpectating(owner);
		else if(slot < players.size())
		{
			Player target = players.get(slot);
			if(target != owner)
			{
				owner.setGameMode(GameMode.SPECTATOR);
				dungeon.Spectate(owner, target);
			}
		}
		e.setCancelled(true);
		owner.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
	}
	
	@Override
	public void closeMenu(InventoryCloseEvent e)
	{
	
	}
	
	@Override
	public void setItems()
	{
		players = dungeon.getPlayers();
		int i = 0;
		for(Player p : players)
		{
			inventory.setItem(i, ItemFactory.makeSkull(p, p.getName()));
			i++;
		}
		inventory.setItem(22, ItemFactory.makeItem(Material.DARK_OAK_DOOR, ChatColor.RED + "Exit"));
	}
}
