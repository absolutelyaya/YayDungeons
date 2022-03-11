package yaya.dungeons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import yaya.dungeons.dungeons.Dungeon;
import yaya.dungeons.utilities.DungeonManager;

public class DeathListener implements Listener
{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		if(DungeonManager.isWorldDungeon(p.getWorld()))
		{
			p.getWorld().strikeLightningEffect(p.getLocation());
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(DungeonManager.isWorldDungeon(p.getWorld()))
		{
			Dungeon d = DungeonManager.getDungeon(DungeonManager.getDungeoneer(p).getCurrentDungeon());
			e.setRespawnLocation(p.getWorld().getSpawnLocation());
			d.Spectate(p);
		}
	}
}
