package yaya.dungeons.dungeons;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import yaya.dungeons.utilities.ItemFactory;

import java.util.UUID;

public class Dungeoneer
{
	private final Player owner;
	private UUID currentDungeon;
	private final YamlConfiguration playerData;
	private Profession profession = Profession.Rookie;
	
	public Dungeoneer(UUID dungeon, Player player)
	{
		this.owner = player;
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
	
	public void setProfession(Profession profession)
	{
		if(this.profession.equals(Profession.Rookie))
		{
			this.profession = profession;
			owner.sendMessage("Your class is now [" + this.profession.label + "]");
			for(ItemStack item : this.profession.equip)
			{
				owner.getInventory().addItem(item);
			}
		}
	}
	
	public Profession getProfession()
	{
		return profession;
	}
	
	public enum Profession
	{
		Rookie("Rookie",
				new ItemStack[] {ItemFactory.makeItem(Material.WOODEN_SWORD, "", true)}),
		Warrior("Warrior",
				new ItemStack[]
						{
								ItemFactory.makeItem(Material.STONE_SWORD, "", true),
								ItemFactory.makeItem(Material.LEATHER_BOOTS, "", true),
								ItemFactory.makeItem(Material.CHAINMAIL_CHESTPLATE, "", true)
						}),
		Archer("Archer",
				new ItemStack[]
						{
								ItemFactory.makeItem(Material.BOW, "", true),
								ItemFactory.makeItem(Material.ARROW, "", 16),
								ItemFactory.makeArmor(Material.LEATHER_CHESTPLATE, "", Color.GREEN, true,
										new Attribute[]
												{
														Attribute.GENERIC_MOVEMENT_SPEED
												},
										new AttributeModifier[]
												{
														new AttributeModifier(UUID.randomUUID(), "speed", 0.025,
																AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST)
												})
						}),
		Healer("Healer",
				new ItemStack[]
						{
								ItemFactory.makeItem(Material.WOODEN_SWORD, "", true),
								ItemFactory.makePotion(Material.SPLASH_POTION, "", Color.RED, 6,
										new PotionEffect[] {new PotionEffect(PotionEffectType.HEAL, 0, 0)}),
								ItemFactory.makePotion(Material.SPLASH_POTION, "", Color.GRAY, 2,
										new PotionEffect[] {new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1)})
						});
		public String label;
		public ItemStack[] equip;
		Profession(String label, ItemStack[] equip)
		{
			this.label = label;
			this.equip = equip;
		}
	}
}
