package yaya.dungeons.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import yaya.dungeons.YayDungeons;
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
			d.addToGraveyard(p);
			p.getWorld().strikeLightningEffect(p.getLocation());
			if(d.isCollapsed())
				e.deathMessage(Component.text(p.getName() + " was torn apart in a Dungeon Collapse."));
			if(d.getPlayers().size() == 0)
				p.getWorld().sendMessage(Component.text(ChatColor.DARK_RED + "The entire Party was wiped out..."));
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
			if(d.isCollapsed() || d.getPlayers().size() == 0)
			{
				Location l = p.getBedSpawnLocation();
				if(l != null)
					e.setRespawnLocation(l);
				else
					e.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
				DungeonManager.leaveDungeon(p, 3);
			}
			else if(d.getPlayers().size() > 0)
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						d.Spectate(p);
					}
				}.runTaskLater(YayDungeons.instance, 2L);
			}
		}
	}
}
