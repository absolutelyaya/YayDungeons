package yaya.dungeons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import yaya.dungeons.utilities.DungeonManager;

public class DeathListener implements Listener
{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		if(DungeonManager.isWorldDungeon(p.getWorld()))
		{
		
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(DungeonManager.isWorldDungeon(p.getWorld()))
		{
			e.setRespawnLocation(p.getWorld().getSpawnLocation());
		}
	}
}
