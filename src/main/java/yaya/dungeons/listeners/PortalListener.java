package yaya.dungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import yaya.dungeons.utilities.DungeonManager;

public class PortalListener implements Listener
{
	@EventHandler
	public void onEnterPortal(PlayerPortalEvent e)
	{
		if(DungeonManager.isWorldDungeon(e.getFrom().getWorld()))
		{
			e.setCancelled(true);
			DungeonManager.leaveDungeon(e.getPlayer(), 2);
		}
	}
	
	@EventHandler
	public void onCreatePortal(PortalCreateEvent e)
	{
		if(DungeonManager.isWorldDungeon(e.getWorld()))
			e.setCancelled(true);
	}
}
