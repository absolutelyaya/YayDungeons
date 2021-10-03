package yaya.dungeons.listeners;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import yaya.dungeons.utilities.DungeonManager;

public class MobSpawnListener implements Listener
{
	@EventHandler
	public void onMobSpawn(EntitySpawnEvent e)
	{
		if(DungeonManager.isWorldDungeon(e.getLocation().getWorld()) && e.getEntityType().equals(EntityType.ENDER_DRAGON))
		{
			EnderDragon entity = (EnderDragon) e.getEntity();
			entity.remove();
		}
	}
}
