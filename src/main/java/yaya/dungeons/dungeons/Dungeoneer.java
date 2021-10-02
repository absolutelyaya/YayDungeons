package yaya.dungeons.dungeons;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class Dungeoneer
{
	private UUID currentDungeon;
	private final YamlConfiguration playerData;
	
	public Dungeoneer(UUID dungeon, Player player)
	{
		currentDungeon = dungeon;
		playerData = new YamlConfiguration();
		saveOutsideInventory(player);
		player.getInventory().clear();
	}
	
	public void saveOutsideInventory(Player p)
	{
		saveInventory(p, "OutsideInventory");
	}
	
	public void loadOutsideInventory(Player p)
	{
		loadInventory(p, "OutsideInventory");
	}
	
	public void saveDungeonInventory(Player p)
	{
		saveInventory(p, "DungeonInventory");
	}
	
	public void loadDungeonInventory(Player p)
	{
		loadInventory(p, "DungeonInventory");
	}
	
	void loadInventory(Player p, String type)
	{
		PlayerInventory inv = p.getInventory();
		for(int i = 0; i < 36; i++)
		{
			inv.setItem(i, playerData.getItemStack(type + "." + p.getUniqueId() + "." + i));
		}
		inv.setItem(EquipmentSlot.HEAD, playerData.getItemStack(type + "." + p.getUniqueId() + ".Helmet"));
		inv.setItem(EquipmentSlot.CHEST, playerData.getItemStack(type + "." + p.getUniqueId() + ".ChestPlate"));
		inv.setItem(EquipmentSlot.LEGS, playerData.getItemStack(type + "." + p.getUniqueId() + ".Pants"));
		inv.setItem(EquipmentSlot.FEET, playerData.getItemStack(type + "." + p.getUniqueId() + ".Shoes"));
		inv.setItem(EquipmentSlot.OFF_HAND, playerData.getItemStack(type + "." + p.getUniqueId() + ".OffHand"));
	}
	
	void saveInventory(Player p, String type)
	{
		PlayerInventory inv = p.getInventory();
		for(int i = 0; i < 36; i++)
		{
			playerData.set(type + "." + p.getUniqueId() + "." + i, inv.getItem(i));
		}
		playerData.set(type + "." + p.getUniqueId() + ".Helmet", inv.getItem(EquipmentSlot.HEAD));
		playerData.set(type + "." + p.getUniqueId() + ".ChestPlate", inv.getItem(EquipmentSlot.CHEST));
		playerData.set(type + "." + p.getUniqueId() + ".Pants", inv.getItem(EquipmentSlot.LEGS));
		playerData.set(type + "." + p.getUniqueId() + ".Shoes", inv.getItem(EquipmentSlot.FEET));
		playerData.set(type + "." + p.getUniqueId() + ".OffHand", inv.getItem(EquipmentSlot.OFF_HAND));
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
