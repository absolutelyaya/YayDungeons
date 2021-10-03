package yaya.dungeons.menus;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import yaya.dungeons.utilities.ItemFactory;

import java.util.*;

public abstract class Menu implements InventoryHolder
{
	public Player owner;
	public Player virtualPlayer;
	protected Inventory inventory;
	protected ItemStack FillerGlass = ItemFactory.makeItem(Material.GRAY_STAINED_GLASS_PANE, "", "");

	public Menu(Player owner, Player virtualPlayer) 
	{
		this.owner = owner;
		if (virtualPlayer != null)
			this.virtualPlayer = virtualPlayer;
		else
			this.virtualPlayer = Bukkit.getPlayer(UUID.randomUUID());
	}

	public abstract String getName();

	public abstract int getSlots();

	public abstract void handleMenu(InventoryClickEvent e);

	public abstract void closeMenu(InventoryCloseEvent e);

	public abstract void setItems();

	public void open() 
	{
		inventory = Bukkit.createInventory(this, getSlots(), getName());
		this.setItems();
		owner.openInventory(inventory);
	}

	@Override
	public Inventory getInventory() 
	{
		return inventory;
	}

	public void fillAir() {
		for (int i = 0; i < getSlots(); i++) 
		{
			if (inventory.getItem(i) == null)
				inventory.setItem(i, FillerGlass);
		}
	}
}
