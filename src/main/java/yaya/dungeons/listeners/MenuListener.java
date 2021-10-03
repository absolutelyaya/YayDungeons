package yaya.dungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import yaya.dungeons.menus.Menu;

public class MenuListener implements Listener {
	@EventHandler
	public void OnMenuAction(InventoryClickEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		if (holder instanceof Menu) {
			if (e.getCurrentItem() == null) {
				return;
			}
			Menu menu = (Menu) holder;
			menu.handleMenu(e);
		}
	}

	@EventHandler
	public void OnMenuClosed(InventoryCloseEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		if (holder instanceof Menu) {
			Menu menu = (Menu) holder;
			menu.closeMenu(e);
		}
	}
}
