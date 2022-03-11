package yaya.dungeons.dungeons;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Dungeoneer
{
	private final Player player;
	private UUID currentDungeon;
	
	public Dungeoneer(UUID dungeon, Player player)
	{
		this.player = player;
		currentDungeon = dungeon;
	}
	
	public UUID getCurrentDungeon()
	{
		return currentDungeon;
	}
	
	public void setCurrentDungeon(UUID dungeon)
	{
		currentDungeon = dungeon;
	}
}
