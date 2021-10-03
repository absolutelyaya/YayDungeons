package yaya.dungeons.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ClassUpgradeMenu extends Menu
{
	public ClassUpgradeMenu(Player owner, Player virtualPlayer)
	{
		super(owner, virtualPlayer);
	}
	
	@Override
	public String getName()
	{
		return null;
	}
	
	@Override
	public int getSlots()
	{
		return 0;
	}
	
	@Override
	public void setItems()
	{
	
	}
	
	@Override
	public void handleMenu(InventoryClickEvent e)
	{
	
	}
	
	@Override
	public void closeMenu(InventoryCloseEvent e)
	{
	
	}
}
