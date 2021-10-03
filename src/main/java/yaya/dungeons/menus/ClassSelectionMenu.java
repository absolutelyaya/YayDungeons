package yaya.dungeons.menus;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import yaya.dungeons.utilities.DungeonManager;
import yaya.dungeons.dungeons.Dungeoneer;
import yaya.dungeons.utilities.ItemFactory;

public class ClassSelectionMenu extends Menu
{
	public ClassSelectionMenu(Player owner, Player virtualPlayer)
	{
		super(owner, virtualPlayer);
	}
	
	@Override
	public String getName()
	{
		return "Class Selection";
	}
	
	@Override
	public int getSlots()
	{
		return 9;
	}
	
	@Override
	public void setItems()
	{
		inventory.setItem(2, ItemFactory.makeItem(Material.IRON_AXE, "Warrior", "Level 1"));
		inventory.setItem(4, ItemFactory.makeItem(Material.BOW, "Archer", "Level 1"));
		inventory.setItem(6, ItemFactory.makePotion(Material.SPLASH_POTION, "Healer", Color.RED,
				new PotionEffect[] { new PotionEffect(PotionEffectType.HEAL, 0, 0) }, "Level 1"));
		fillAir();
	}
	
	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		switch(e.getSlot())
		{
			case 2:
				DungeonManager.getDungeoneer(owner).setProfession(Dungeoneer.Profession.Warrior);
				owner.closeInventory();
				break;
			case 4:
				DungeonManager.getDungeoneer(owner).setProfession(Dungeoneer.Profession.Archer);
				owner.closeInventory();
				break;
			case 6:
				DungeonManager.getDungeoneer(owner).setProfession(Dungeoneer.Profession.Healer);
				owner.closeInventory();
				break;
		}
		e.setCancelled(true);
	}
	
	@Override
	public void closeMenu(InventoryCloseEvent e)
	{
		Dungeoneer d = DungeonManager.getDungeoneer(owner);
		if(d.getProfession().equals(Dungeoneer.Profession.Rookie))
		{
			d.setProfession(Dungeoneer.Profession.Rookie);
		}
	}
}
