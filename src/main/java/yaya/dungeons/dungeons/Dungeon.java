package yaya.dungeons.dungeons;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sk89q.jchronic.utils.Range;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import yaya.dungeons.YayDungeons;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("unused")
public class Dungeon
{
	static Logger logger = null;
	
	private final UUID id;
	private final List<Player> players = new ArrayList<>();
	private final List<Player> spectators = new ArrayList<>();
	private final List<UUID> graveyard = new ArrayList<>();
	private final HashMap<Player, Dungeoneer> savedDungeoneers = new HashMap<>();
	private final Random random;
	private final int seed;
	private final Type type;
	private final List<Modifier> mods = new ArrayList<>();
	private final Queue<DungeonRoom> roomGenQueue = new ArrayDeque<>();
	
	private World world;
	private BossBar bossBar;
	private int lifetime = -1;
	private int unstableCountdownID = -1;
	private boolean collapsed;
	private Player leader;
	
	public Dungeon(UUID id, int seed)
	{
		if(logger == null)
			logger = YayDungeons.instance.getLogger();
		this.id = id;
		this.seed = seed;
		random = new Random(seed);
		//type = Type.values()[random.nextInt(Type.values().length)];
		type = Type.Test; //I just want to get one type working for now.
		logger.info("created dungeon of type " + type);
		generate();
	}
	
	public Dungeon(UUID id, int seed, Type type)
	{
		if(logger == null)
			logger = YayDungeons.instance.getLogger();
		this.id = id;
		this.seed = seed;
		random = new Random(seed);
		this.type = type;
		logger.info(String.valueOf(type));
		generate();
	}
	
	void generate()
	{
		logger.info("Starting generation of Dungeon " + id);
		Bukkit.broadcast(Component.text("Generating a dungeon..."));
		String name;
		WorldCreator wc = new WorldCreator(name = "Dungeon-" + type.name() + "." + id.toString());
		bossBar = BossBar.bossBar(Component.text(name), 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
		wc.type(WorldType.FLAT);
		wc.generatorSettings("{\"structures\": {\"structures\": {\"village\": {\"salt\": 8015723, \"spacing\": 32, \"separation\": 8}}}, " +
									 "\"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\": \"" + type.biome + "\"}");
		wc.generateStructures(false);
		world = wc.createWorld();
		DungeonRoom root = new DungeonRoom(null, 0, BlockVector3.at(0, 63, 0), BlockVector3.ZERO, true, "Entrance");
		roomGenQueue.add(root);
		int roomAttempts = 0;
		int error = 0;
		while(roomGenQueue.size() > 0)
		{
			System.out.println("attempt number " + roomAttempts);
			DungeonRoom room = roomGenQueue.remove();
			if ((error = placeRoom(room)) > 1)
			{
				Bukkit.broadcast(Component.text("Something went wrong during Dungeon generation. Error Code " + error));
				logger.warning("Generation of Dungeon " + id + " failed! Error code: " + error);
				world.getBlockAt(0, 63, 0).setType(Material.GLASS);
				break;
			}
			else if(error == 1) //retry
			{
				System.out.println("room blocked");
				roomAttempts++;
				if(roomAttempts < 3)
				{
					roomGenQueue.add(room);
					System.out.println("retrying");
				}
				else
				{
					roomAttempts = 0;
					//room.parent.remove();
					//room.parent.generationAttempts++;
					//roomGenQueue.add(room.parent);
					System.out.println("retrying failed");
				}
			}
			else
			{
				roomAttempts = 0;
				System.out.println("room placed");
			}
		}
		if(error == 0)
			Bukkit.broadcast(Component.text("Dungeon Generation Complete!"));
		world.setFullTime(random.nextInt((int)(type.time.getEnd() + 1 - type.time.getBegin())) + type.time.getBegin());
		world.setStorm(type.storm);
		world.setThundering(type.thunder);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.SPAWN_RADIUS, 0);
		world.setSpawnLocation(new Location(world, 0.5, 64, 0.5));
		for(Item i : world.getEntitiesByClass(Item.class))
		{
			i.remove();
		}
		
		logger.info("Generation of Dungeon " + id + " Complete! Thinking of modifiers...");
		float modChance = 1f; //random.nextFloat(0.5f);
		for (int attempts = random.nextInt(4); attempts > 0; attempts--)
		{
			if(random.nextFloat() < modChance)
			{
				Modifier newMod = Modifier.RandomWithWeight(random, mods);
				for(Modifier mc : newMod.conflicts)
				{
					if(mods.contains(mc))
					{
						if(newMod.weight < mc.weight)
						{
							mods.remove(mc);
							mods.add(newMod);
						}
					}
					else
						mods.add(newMod);
				}
				if(newMod.conflicts.length == 0)
					mods.add(newMod);
				modChance /= mods.size() + 0.5;
			}
		}
		int highestPriority = 0;
		for(Modifier m : mods)
		{
			if(m.priority > highestPriority)
			{
				bossBar.color(m.color);
				highestPriority = m.priority;
			}
		}
		world.setGameRule(GameRule.KEEP_INVENTORY, mods.contains(Modifier.Keeping));
		if(mods.contains(Modifier.Unstable))
			StartUnstableTimer();
		logger.info(mods.size() + " Modifiers selected.");
		
		//close all rooms editSessions since the dungeon has finished generating.
		root.close();
	}
	
	public void SaveDungeoneer(Player p, Dungeoneer d)
	{
		savedDungeoneers.put(p, d);
	}
	
	@SuppressWarnings("ConstantConditions")
	short placeRoom(DungeonRoom room)
	{
		String roomSchem = getRoomSchem(type, room);
		BlockVector3 pos = room.pos.add(room.offset);
		double rot = room.rot * 90;
		boolean includeEntities = room.includeEntities;
		Clipboard c;
		GZIPInputStream zipStream;
		try
		{
			File tmp = File.createTempFile("tmp", "schem");
			GZIPOutputStream zipOut = new GZIPOutputStream(new FileOutputStream(tmp));
			InputStream inStream = getClass().getClassLoader().getResourceAsStream(roomSchem);
			int b;
			if(inStream == null)
				return 2;
			while((b = inStream.read()) != -1)
			{
				zipOut.write((byte)b);
			}
			zipOut.finish();
			zipOut.close();
			zipStream = new GZIPInputStream(new FileInputStream(tmp));
			tmp.delete();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return 3;
		}
		try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(zipStream))
		{
			c = reader.read();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return 4;
		}
		EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
		room.setSession(session);
		try
		{
			Plugin worldEditPlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
			if(worldEditPlugin == null)
				return 5;
			
			ClipboardHolder ch = new ClipboardHolder(c);
			ch.setTransform(new AffineTransform().rotateY(rot));
			Operation op = ch.createPaste(session).ignoreAirBlocks(true).copyEntities(includeEntities).to(pos).build();
			
			Region roomRegion = c.getRegion().clone();
			roomRegion.shift(pos.subtract(ch.getClipboard().getOrigin()));
			roomRegion = new CuboidRegion(rotatePointAround(roomRegion.getMinimumPoint(), pos.getX(), pos.getZ(), rot / 180.0),
				rotatePointAround(roomRegion.getMaximumPoint(), pos.getX(), pos.getZ(), rot / 180.0));
			if (session.countBlocks(roomRegion, new BlockMask(session, BlockTypes.STRUCTURE_VOID.getDefaultState().toBaseBlock())) > 0)
			{
				//session.replaceBlocks(roomRegion, new BlockMask(session, BlockTypes.STRUCTURE_VOID.getDefaultState().toBaseBlock()),
				//		BlockTypes.RED_STAINED_GLASS.getDefaultState());
				op.cancel();
				session.close();
				return 1; //retry
			}
			Operations.complete(op);
			
			Mask mask = new BlockMask(session, BlockTypes.AIR.getDefaultState().toBaseBlock(), BlockTypes.GLASS.getDefaultState().toBaseBlock());
			session.setMask(mask);
			Region wallRegion = roomRegion.clone();
			wallRegion.expand(BlockVector3.ONE);
			wallRegion.expand(BlockVector3.ONE.multiply(-1));
			session.makeWalls(wallRegion, BlockTypes.GLASS.getDefaultState());
			
			Operations.complete(session.commit());
			roomRegion.expand(BlockVector3.at(0, 1, 0));
			roomRegion.expand(BlockVector3.at(0, -1, 0));
			session.replaceBlocks(roomRegion, mask, BlockTypes.STRUCTURE_VOID.getDefaultState().toBaseBlock());
			session.close();
			
			for(BlockVector3 b : wallRegion) //get all Signs and process them
			{
				Block block = world.getBlockAt(b.getBlockX(), b.getBlockY(), b.getBlockZ());
				if(block.getType().equals(Material.OAK_SIGN))
				{
					processSign((Sign)block.getState(), room);
					block.setType(Material.STRUCTURE_VOID);
				}
			}
		}
		catch (WorldEditException e)
		{
			e.printStackTrace();
			return 6;
		}
		return 0;
	}
	
	boolean processSign(Sign sign, DungeonRoom room)
	{
		List<String> lines = new ArrayList<>();
		for(Component comp : sign.lines())
		{
			JsonElement e = JsonParser.parseString(ComponentSerializer.toString(comp));
			if(e.isJsonObject())
			{
				lines.add(e.getAsJsonObject().get("content").getAsString());
			}
		}
		if(lines.size() == 0)
			return false;
		switch(lines.get(0))
		{
			case "Room" -> {
				try
				{
					int rot = 2 - (Integer.parseInt(sign.getBlockData().getAsString().split("rotation=|,")[1]) / 4);
					Location loc = sign.getLocation();
					Vector dir = ((org.bukkit.block.data.type.Sign)sign.getBlockData()).getRotation().getOppositeFace()
							.getDirection().multiply(Integer.parseInt(lines.get(1)));
					boolean includeEntities = false;
					if(lines.get(3).length() > 0)
						includeEntities = lines.get(3).contains("-e");
					if(lines.get(2).length() > 0)
						roomGenQueue.add(new DungeonRoom(room, rot, BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
								BlockVector3.at(dir.getX(), dir.getY(), dir.getZ()), includeEntities, lines.get(2)));
					else
						roomGenQueue.add(new DungeonRoom(room, rot, BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
								BlockVector3.at(dir.getX(), dir.getY(), dir.getZ()), includeEntities));
					}
				catch (Exception e)
				{
					logger.info("Error processing sign at " + sign.getLocation() + ":");
					e.printStackTrace();
					return false;
				}
			}
			case "Flag" -> {
				//TODO
			}
		}
		
		return true;
	}
	
	String getRoomSchem(Type type, DungeonRoom room)
	{
		try
		{
			List<String> results = new ArrayList<>();
			ClassLoader cl = Dungeon.class.getClassLoader();
			URL resource = cl.getResource("Rooms/" + type.name() + "/" + room.roomType);
			if(resource != null)
			{
				Map<String, String> env = new HashMap<>();
				String[] array = resource.toString().split("!");
				FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
				Path path = fs.getPath(array[1]);
				for(Path p : Files.walk(path).toList())
				{
					if(p.getFileName().toString().endsWith(".schem"))
						results.add(p.getFileName().toString());
				}
				if(fs.isOpen())
					fs.close();
				return "Rooms/" + type.name() + "/" + room.roomType + "/" + results.get(random.nextInt(results.size()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	
	private static BlockVector3 rotatePointAround(BlockVector3 point, int centerX, int centerZ, double rot) {
		double angle = -rot * Math.PI;
		
		double rotatedX = Math.cos(angle) * (point.getX() - centerX) - Math.sin(angle) * (point.getZ() - centerZ) + centerX;
		double rotatedZ = Math.sin(angle) * (point.getX() - centerX) + Math.cos(angle) * (point.getZ() - centerZ) + centerZ;
		
		return BlockVector3.at(rotatedX, point.getBlockY(), rotatedZ);
	}
	
	public void CloseDungeon()
	{
		logger.info("Closing Dungeon " + id);
		List<Player> allPlayers = world.getPlayers();
		allPlayers.addAll(spectators);
		for (Player p : allPlayers)
		{
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			p.sendMessage(ChatColor.RED + "The Dungeon instance you were in was closed.");
			p.hideBossBar(bossBar);
		}
		Bukkit.unloadWorld(world, false);
		world.setAutoSave(false);
		deleteRecursively(world.getWorldFolder());
	}
	
	public void Enter(Player p)
	{
		for (Player pl : players)
			pl.sendMessage(Component.text(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " has entered the Dungeon."));
		players.add(p);
		p.teleport(world.getSpawnLocation());
		p.sendMessage(ChatColor.GRAY + "You entered the Dungeon.");
		p.sendMessage(ChatColor.GRAY + "Seed: " + seed);
		p.showBossBar(bossBar);
		DisplayMods(p);
		if(leader == null)
			setLeader(p);
	}
	
	@CanIgnoreReturnValue
	public boolean Spectate(Player p)
	{
		if(players.remove(p))
			for (Player pl : players)
				pl.sendMessage(Component.text(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " has left the Dungeon."));
		return Spectate(p, players.get(random.nextInt(players.size())));
	}
	
	public boolean Spectate(Player p, Player target)
	{
		p.showBossBar(bossBar);
		if(players.remove(p))
			for (Player pl : players)
				pl.sendMessage(Component.text(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " has left the Dungeon."));
		if(p != target)
		{
			p.setGameMode(GameMode.SPECTATOR);
			p.setSpectatorTarget(target);
			p.sendMessage(ChatColor.GRAY + "You're now spectating " + target.getName() + ".");
			if(!spectators.contains(p))
				spectators.add(p);
			return true;
		}
		else
		{
			p.sendMessage(Component.text("You cannot spectate yourself!"));
			return false;
		}
	}
	
	public void DisplayMods(Player p)
	{
		for(int i = 0; i < mods.size(); i++)
		{
			Modifier m = mods.get(i);
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					p.resetTitle();
					p.sendTitlePart(TitlePart.TITLE, m.title);
					p.sendTitlePart(TitlePart.SUBTITLE, m.description);
					p.playSound(p, m.sound, 2f, 0.5f);
				}
			}.runTaskLater(YayDungeons.instance, 60L * i + 20L);
		}
	}
	
	public void DisplayModsSimple(Player p)
	{
		p.sendMessage("------ Dungeon Mods ------");
		for (Modifier m : mods)
		{
			p.sendMessage(Component.text(" - ").append(m.title.hoverEvent(m.description.asHoverEvent())));
		}
	}
	
	public void StopSpectating(Player p)
	{
		if (spectators.remove(p))
		{
			Location l = p.getBedSpawnLocation();
			if(l != null)
				p.teleport(l);
			else
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			p.setGameMode(GameMode.SURVIVAL);
			p.hideBossBar(bossBar);
		}
	}
	
	public boolean Leave(Player p, int method)
	{
		if(!p.isOp())
		{
			if(method == 1 && mods.contains(Modifier.Gripping))
			{
				p.sendMessage(Component.text("Can't leave due to the ").color(TextColor.color(0xFB5454))
						.append(Component.text("Gripping")
								.style(Style.empty().decoration(TextDecoration.BOLD, true).color(TextColor.color(0xA80000))))
						.append(Component.text(" Modifier!")));
				return false;
			}
			else if((method == 1 || method == 2) && mods.contains(Modifier.Locked))
			{
				p.sendMessage(Component.text("Can't leave due to the ").color(TextColor.color(0xFB5454))
						.append(Component.text("Locked")
								.style(Style.empty().decoration(TextDecoration.BOLD, true).color(TextColor.color(0xA80000))))
						.append(Component.text(" Modifier!")));
				return false;
			}
		}
		players.remove(p);
		for (Player pl : players)
			pl.sendMessage(Component.text(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " has left the Dungeon."));
		Location l = p.getBedSpawnLocation();
		if(l != null)
			p.teleport(l);
		else
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		if(players.size() == 0)
			CloseDungeon();
		else if(leader == p)
			setLeader(players.get(random.nextInt(players.size())));
		p.hideBossBar(bossBar);
		return true;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public List<Player> getPlayers()
	{
		List<Player> playerList = new ArrayList<>(players);
		for (UUID id : graveyard)
		{
			Player p = Bukkit.getPlayer(id);
			playerList.remove(p);
		}
		return playerList;
	}
	
	public List<Player> getSpectators()
	{
		return spectators;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public HashMap<Player, Dungeoneer> getSavedDungeoneers()
	{
		return savedDungeoneers;
	}
	
	public boolean isCollapsed()
	{
		return collapsed;
	}
	
	public boolean isCanJoin(Player p)
	{
		return !graveyard.contains(p.getUniqueId());
	}
	
	public Player getLeader()
	{
		return leader;
	}
	
	void setLeader(Player p)
	{
		leader = p;
		world.sendMessage(Component.text(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " is now the Dungeon Explorations Leader!"));
	}
	
	public void addToGraveyard(Player p)
	{
		graveyard.add(p.getUniqueId());
	}
	
	public static void deleteRecursively(File directory)
	{
		if(directory.exists() && directory.listFiles() != null)
		{
			for (File file : Objects.requireNonNull(directory.listFiles()))
			{
				if (file.isDirectory())
				{
					deleteRecursively(file);
				}
				else
				{
					file.delete();
				}
			}
			directory.delete();
		}
	}
	
	void StartUnstableTimer()
	{
		bossBar.addFlag(BossBar.Flag.DARKEN_SCREEN);
		lifetime = 910;
		unstableCountdownID = Bukkit.getScheduler().scheduleSyncRepeatingTask(YayDungeons.instance, () -> {
			lifetime--;
			int seconds = lifetime % 60;
			world.sendActionBar(Component.text(lifetime / 60 + ":" + (seconds > 9 ? "" : "0") + seconds + " until Dungeon Collapse!")
					.color(TextColor.color(0xA80000)));
			if(lifetime % 300 == 0)
			{
				world.sendMessage(Component.text(lifetime / 60 + " Minutes remaining until collapse.").color(TextColor.color(0xFB5454)));
				world.playSound(world.getSpawnLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 100f, 0.5f);
				UnstableScreenshake(0.75f, 20);
			}
			if(lifetime == 60)
			{
				world.sendMessage(Component.text("Collapse imminent. 1 Minute remaining!").color(TextColor.color(0xA80000)));
				world.playSound(world.getSpawnLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 100f, 0.5f);
				UnstableScreenshake(1.25f, 20);
			}
			if(lifetime == 30)
			{
				world.sendMessage(Component.text("The Air begins to quiver. Collapse in 30 Seconds!").color(TextColor.color(0xA80000)));
				world.playSound(world.getSpawnLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 100f, 0.5f);
				world.playSound(world.getSpawnLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 100f, 0.5f);
				UnstableScreenshake(2f, 30);
			}
			if(lifetime < 10 && lifetime >= 0)
			{
				world.sendMessage(Component.text("Collapse in " + lifetime + " Seconds!").color(TextColor.color(0xA80000)));
				world.playSound(world.getSpawnLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 100f, 0.5f);
				UnstableScreenshake(3f, 10);
			}
			if(lifetime == 0)
			{
				UnstableCollapse();
			}
		}, 0L, 4L);
	}
	
	void UnstableCollapse()
	{
		collapsed = true;
		Bukkit.getScheduler().cancelTask(unstableCountdownID);
		for (Player p : players)
		{
			p.damage(999999);
		}
	}
	
	void UnstableScreenshake(float severity, long duration)
	{
		Random shaker = new Random();
		int shake = Bukkit.getScheduler().scheduleSyncRepeatingTask(YayDungeons.instance, () -> {
			for (Player p : players)
			{
				Location loc = p.getLocation();
				loc.setYaw(loc.getYaw() + (shaker.nextFloat() - 0.5f) * severity);
				loc.setPitch(loc.getPitch() + (shaker.nextFloat() - 0.5f) * severity);
				p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}, 0, 1);
		Bukkit.getScheduler().scheduleSyncDelayedTask(YayDungeons.instance, () -> Bukkit.getScheduler().cancelTask(shake),
				duration);
	}
	
	public enum Type
	{
		Test("the_void", new Range(0, 24000), false, false, Material.WHITE_CONCRETE),
		Nature("forest", new Range(0, 24000), false, false, Material.OAK_SAPLING),
		Desert("desert", new Range(0, 24000), false, false, Material.CACTUS),
		Castle("the_void", new Range(18000, 18000), false, false, Material.STONE_BRICKS),
		Ashen("basalt_deltas", new Range(0, 24000), true, false, Material.DEAD_BUSH),
		Demonic("crimson_forest", new Range(18000, 18000), true, false, Material.CRIMSON_HYPHAE),
		Mansion("dark_forest", new Range(12500, 23500), true, true, Material.DARK_OAK_SAPLING);
		
		public final String biome;
		public final Range time;
		public final boolean storm;
		public final boolean thunder;
		public final Material icon;
		Type(String biome, Range time, boolean storm, boolean thunder, Material icon)
		{
			this.biome = biome;
			this.time = time;
			this.storm = storm;
			this.thunder = thunder;
			this.icon = icon;
		}
	}
	
	public enum Modifier
	{
		Gripping(5, 1, Color.BLUE, Sound.ITEM_BONE_MEAL_USE, Component.text(ChatColor.YELLOW + "Gripping"),
				Component.text("You can only leave using Portals.")),
		Keeping(2, 0, Color.GREEN, Sound.ITEM_ARMOR_EQUIP_GOLD, Component.text(ChatColor.DARK_GREEN + "Keeping"),
				Component.text("You keep your items upon death!")),
		Evil(3, 1, Color.RED, Sound.ENTITY_VEX_CHARGE, Component.text(ChatColor.DARK_RED + "Evil"),
				Component.text("The mobs in here look stronger than usual...")),
		Locked(4, 2, Color.PINK, Sound.BLOCK_CONDUIT_DEACTIVATE, Component.text(ChatColor.LIGHT_PURPLE + "Locked"),
				Component.text("You can only leave when the boss has been killed.")),
		Generous(3, 0, Color.GREEN, Sound.ENTITY_PLAYER_LEVELUP, Component.text(ChatColor.GOLD + "Generous"),
				Component.text("More loot!"), Keeping),
		Full(3, 1, Color.RED, Sound.AMBIENT_CAVE, Component.text(ChatColor.RED + "Full"),
				Component.text("There are more mobs in here than usual...")),
		Unstable(1, 3, Color.PINK, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,
				Component.text("#").style(Style.style(TextColor.color(0x9E0484), TextDecoration.OBFUSCATED))
						.append(Component.text(" Unstable ").style(Style.empty().decoration(TextDecoration.OBFUSCATED, false)))
						.append(Component.text("#")),
				Component.text(ChatColor.RED + "This Dungeon will collapse in 15 minutes!"), Keeping);
		
		//TODO: Functionality of Evil, Generous and Full
		
		public final int weight;
		public final int priority;
		public final Color color;
		public final Sound sound;
		public final Component title;
		public final Component description;
		public final Modifier[] conflicts;
		Modifier(int weight, int priority, Color color, Sound sound, Component title, Component description, Modifier... conflicts)
		{
			this.weight = weight;
			this.priority = priority;
			this.color = color;
			this.sound = sound;
			this.title = title;
			this.description = description;
			this.conflicts = conflicts;
		}
		
		public static Modifier RandomWithWeight(Random r, List<Modifier> current)
		{
			List<Modifier> weightedValues = new ArrayList<>();
			List<Modifier> exceptions = new ArrayList<>(current);
			for(Modifier m : current)
			{
				exceptions.addAll(Arrays.stream(m.conflicts).toList());
			}
			for(Modifier m : Modifier.values())
			{
				if(!exceptions.contains(m))
				{
					for(int i = 0; i < m.weight; i++)
						weightedValues.add(m);
				}
			}
			return weightedValues.get(r.nextInt(weightedValues.size()));
		}
	}
}
