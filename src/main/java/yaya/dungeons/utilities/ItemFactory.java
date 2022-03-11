package yaya.dungeons.utilities;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class ItemFactory
{
	public static ItemStack makeItem(Material mat, String name, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if(name.length() > 0)
			meta.displayName(Component.text(ChatColor.RESET + name));
		if (lore.length > 0)
		{
			List<Component> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(Component.text(ChatColor.RESET + s));
			}
			meta.lore(lor);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeItem(Material mat, String name, int count, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if(name.length() > 0)
			meta.displayName(Component.text(ChatColor.RESET + name));
		if (lore.length > 0)
		{
			List<Component> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(Component.text(ChatColor.RESET + s));
			}
			meta.lore(lor);
		}
		item.setItemMeta(meta);
		item.setAmount(count);
		return item;
	}
	
	public static ItemStack makeItem(Material mat, String name, boolean unbreakable, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if(name.length() > 0)
			meta.displayName(Component.text(ChatColor.RESET + name));
		if (lore.length > 0)
		{
			List<Component> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(Component.text(ChatColor.RESET + s));
			}
			meta.lore(lor);
		}
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeSkull(OfflinePlayer owner, String name, String... lore)
	{
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if(name.length() > 0)
			meta.displayName(Component.text(ChatColor.RESET + name));
		if (lore.length > 0)
		{
			List<Component> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(Component.text(ChatColor.RESET + s));
			}
			meta.lore(lor);
		}
		meta.setOwningPlayer(owner);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makePotion(Material type, String name, Color color, PotionEffect[] effects, String... lore)
	{
		if(type.equals(Material.POTION) || type.equals(Material.LINGERING_POTION) || type.equals(Material.SPLASH_POTION))
		{
			ItemStack pot = new ItemStack(type);
			PotionMeta meta = (PotionMeta)pot.getItemMeta();
			meta.setColor(color);
			if(name.length() > 0)
				meta.displayName(Component.text(ChatColor.RESET + name));
			for(PotionEffect effect : effects)
			{
				meta.addCustomEffect(effect, true);
			}
			if (lore.length > 0)
			{
				List<Component> lor = new ArrayList<>();
				for (String s : lore)
				{
					lor.add(Component.text(ChatColor.RESET + s));
				}
				meta.lore(lor);
			}
			pot.setItemMeta(meta);
			return pot;
		}
		else
			return null;
	}
	
	public static ItemStack makePotion(Material type, String name, Color color, int count, PotionEffect[] effects, String... lore)
	{
		if(type.equals(Material.POTION) || type.equals(Material.LINGERING_POTION) || type.equals(Material.SPLASH_POTION))
		{
			ItemStack pot = new ItemStack(type);
			PotionMeta meta = (PotionMeta)pot.getItemMeta();
			meta.setColor(color);
			if(name.length() > 0)
				meta.displayName(Component.text(ChatColor.RESET + name));
			for(PotionEffect effect : effects)
			{
				meta.addCustomEffect(effect, true);
			}
			if (lore.length > 0)
			{
				List<Component> lor = new ArrayList<>();
				for (String s : lore)
				{
					lor.add(Component.text(ChatColor.RESET + s));
				}
				meta.lore(lor);
			}
			pot.setItemMeta(meta);
			pot.setAmount(count);
			return pot;
		}
		else
			return null;
	}
	
	public static ItemStack makeArmor(Material mat, String name, boolean unbreakable, Attribute[] attributes, AttributeModifier[] modifiers, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if(name.length() > 0)
			meta.displayName(Component.text(ChatColor.RESET + name));
		if (lore.length > 0)
		{
			List<Component> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(Component.text(ChatColor.RESET + s));
			}
			meta.lore(lor);
		}
		for (int i = 0; i < attributes.length; i++)
		{
			meta.addAttributeModifier(attributes[i], modifiers[i]);
		}
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeArmor(Material mat, String name, Color color, boolean unbreakable, Attribute[] attributes, AttributeModifier[] modifiers, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		if(name.length() > 0)
			meta.displayName(Component.text(ChatColor.RESET + name));
		if (lore.length > 0)
		{
			List<Component> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(Component.text(ChatColor.RESET + s));
			}
			meta.lore(lor);
		}
		for (int i = 0; i < attributes.length; i++)
		{
			meta.addAttributeModifier(attributes[i], modifiers[i]);
		}
		meta.setUnbreakable(unbreakable);
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
}
