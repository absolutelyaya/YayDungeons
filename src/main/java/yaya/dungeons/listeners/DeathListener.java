package yaya.dungeons.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
			Dungeon d = DungeonManager.getDungeon(DungeonManager.getDungeoneer(p).getCurrentDungeon());
			p.getWorld().strikeLightningEffect(p.getLocation());
			if(d.isCollapsed())
				e.deathMessage(Component.text(p.getName() + " was torn apart in a Dungeon Collapse."));
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(DungeonManager.isWorldDungeon(p.getWorld()))
		{
			Dungeon d = DungeonManager.getDungeon(DungeonManager.getDungeoneer(p).getCurrentDungeon());
			e.setRespawnLocation(p.getWorld().getSpawnLocation());
			if(d.isCollapsed())
			{
				Location l = p.getBedSpawnLocation();
				if(l != null)
					e.setRespawnLocation(l);
				else
					e.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
				d.Leave(p, 3);
			}
			else
				d.Spectate(p);
		}
	}
}
