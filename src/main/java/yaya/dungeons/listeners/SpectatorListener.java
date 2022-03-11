package yaya.dungeons.listeners;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import yaya.dungeons.menus.SpectatorMenu;
import yaya.dungeons.utilities.DungeonManager;

import java.util.UUID;

public class SpectatorListener implements Listener
{
	@EventHandler
	public void OnStopSpectating(PlayerStopSpectatingEntityEvent e)
	{
		Player p = e.getPlayer();
		if(DungeonManager.isWorldDungeon(p.getWorld()))
		{
			new SpectatorMenu(p, p, DungeonManager.getDungeon(UUID.fromString(p.getWorld().getName().split("\\.")[1]))).open();
		}
	}
}
