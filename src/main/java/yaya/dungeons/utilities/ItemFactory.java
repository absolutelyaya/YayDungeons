package yaya.dungeons.utilities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.*;

public class ItemFactory
{
	public static ItemStack makeItem(Material mat, String name, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if(name.length() > 0)
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			List<String> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(ChatColor.RESET + s);
			}
			meta.setLore(lor);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeItem(Material mat, String name, int count, String... lore)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if(name.length() > 0)
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			List<String> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(ChatColor.RESET + s);
			}
			meta.setLore(lor);
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
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			List<String> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(ChatColor.RESET + s);
			}
			meta.setLore(lor);
		}
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeSkull(String url, String name, String... lore)
	{
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if(name.length() > 0)
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			List<String> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(ChatColor.RESET + s);
			}
			meta.setLore(lor);
		}
		UUID uuid = UUID.randomUUID();
		GameProfile profile = new GameProfile(uuid, "randy" + uuid);
		String texture = new String(Base64.getEncoder()
				.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[] { url }).getBytes()));
		profile.getProperties().put("textures", new Property("textures", texture));
		Field profileField = FieldUtils.getField(meta.getClass(), "profile", true);
		try {
			profileField.set(meta, profile);
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		meta.getPersistentDataContainer().set(NamespacedKey.fromString("display"), PersistentDataType.STRING,
				"SkullOwner:" + GameProfileSerializer.serialize(new NBTTagCompound(), profile));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeSkull(OfflinePlayer owner, String name, String... lore)
	{
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if(name.length() > 0)
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			for (String s : lore)
			{
				s = ChatColor.RESET + s;
			}
			meta.setLore(Arrays.asList(lore));
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
				meta.setDisplayName(ChatColor.RESET + name);
			for(PotionEffect effect : effects)
			{
				meta.addCustomEffect(effect, true);
			}
			if (lore.length > 0)
			{
				
				List<String> lor = new ArrayList<>();
				for (String s : lore)
				{
					lor.add(ChatColor.RESET + s);
				}
				meta.setLore(lor);
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
				meta.setDisplayName(ChatColor.RESET + name);
			for(PotionEffect effect : effects)
			{
				meta.addCustomEffect(effect, true);
			}
			if (lore.length > 0)
			{
				
				List<String> lor = new ArrayList<>();
				for (String s : lore)
				{
					lor.add(ChatColor.RESET + s);
				}
				meta.setLore(lor);
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
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			List<String> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(ChatColor.RESET + s);
			}
			meta.setLore(lor);
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
			meta.setDisplayName(ChatColor.RESET + name);
		if (lore.length > 0)
		{
			List<String> lor = new ArrayList<>();
			for (String s : lore)
			{
				lor.add(ChatColor.RESET + s);
			}
			meta.setLore(lor);
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
