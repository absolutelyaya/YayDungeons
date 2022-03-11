package yaya.dungeons.menus;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.utilities.DungeonManager;
import yaya.dungeons.utilities.ItemFactory;

import java.util.List;
import java.util.UUID;

public class DungeonBrowserMenu extends Menu
{
	List<Dungeon> dungeons;
	
	public DungeonBrowserMenu(Player owner, Player virtualPlayer)
	{
		super(owner, virtualPlayer);
		dungeons = DungeonManager.getDungeons();
	}
	
	@Override
	public String getName()
	{
		return "Dungeon Browser";
	}
	
	@Override
	public int getSlots()
	{
		return 54;
	}
	
	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		if(inventory.getItem(e.getSlot()) != null)
		{
			switch (e.getClick())
			{
				case LEFT -> DungeonManager.sendJoinRequest(owner, UUID.fromString(dungeons.get(e.getSlot()).getWorld().getName().split("\\.")[1]));
				case RIGHT -> new SpectatorMenu(owner, owner, dungeons.get(e.getSlot())).open();
			}
		}
		owner.playSound(owner, Sound.UI_BUTTON_CLICK, 1, 1);
		e.setCancelled(true);
	}
	
	@Override
	public void closeMenu(InventoryCloseEvent e)
	{
	
	}
	
	@Override
	public void setItems()
	{
		int i = 0;
		for(Dungeon d : dungeons)
		{
			inventory.setItem(i, ItemFactory.makeItem(d.getType().icon, d.getWorld().getName(),
					"Leader: " + d.getLeader().getName(), "", "Left Click to send a Join Request", "Right Click to spectate"));
			i++;
		}
	}
}
