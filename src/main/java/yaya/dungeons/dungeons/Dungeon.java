package yaya.dungeons.dungeons;

import com.sk89q.jchronic.utils.Range;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import yaya.dungeons.YayDungeons;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Dungeon
{
	static Logger logger = null;
	
	private final UUID id;
	private final int sizeX;
	private final int sizeY;
	private final List<Player> players = new ArrayList<>();
	private final List<Player> spectators = new ArrayList<>();
	private final HashMap<Player, Dungeoneer> savedDungeoneers = new HashMap<>();
	private final Random random;
	private final int seed;
	private final Type type;
	
	private World world;
	
	public Dungeon(UUID id, int sizeX, int sizeY, int seed)
	{
		if(logger == null)
			logger = YayDungeons.instance.getLogger();
		this.id = id;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.seed = seed;
		random = new Random(seed);
		//type = Type.values()[random.nextInt(Type.values().length)];
		type = Type.Nature; //I just want to get one type working for now.
		logger.info(String.valueOf(type));
		generate();
	}
	
	void generate()
	{
		Bukkit.broadcastMessage("Generating a dungeon...");
		WorldCreator wc = new WorldCreator("Dungeon-" + id.toString());
		wc.type(WorldType.FLAT);
		wc.generatorSettings("{\"structures\": {\"structures\": {\"village\": {\"salt\": 8015723, \"spacing\": 32, \"separation\": 8}}}, " +
									 "\"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\": \"" + type.biome + "\"}");
		wc.generateStructures(false);
		world = wc.createWorld();
		int error;
		if((error = placeRoom("Rooms/NatureEntrance.schem", BlockVector3.at(0,63,0), 0, false)) == 0)
			Bukkit.broadcastMessage("Dungeon Generation Complete!");
		else
		{
			Bukkit.broadcastMessage("Something went wrong during Dungeon generation. Error Code " + error);
			world.getBlockAt(0, 63, 0).setType(Material.GLASS);
		}
		world.setFullTime(random.nextInt((int)(type.time.getEnd() + 1 - type.time.getBegin())) + type.time.getBegin());
		world.setStorm(type.storm);
		world.setThundering(type.thunder);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		for(Item i : world.getEntitiesByClass(Item.class))
		{
			i.remove();
		}
	}
	
	public void SaveDungeoneer(Player p, Dungeoneer d)
	{
		savedDungeoneers.put(p, d);
	}
	
	short placeRoom(String room, BlockVector3 pos, double rot, boolean includeEntities)
	{
		Clipboard c;
		GZIPInputStream zipStream;
		try
		{
			File tmp = File.createTempFile("tmp", "schem");
			GZIPOutputStream zipOut = new GZIPOutputStream(new FileOutputStream(tmp));
			InputStream inStream = getClass().getClassLoader().getResourceAsStream(room);
			int b;
			if(inStream == null)
				return 1;
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
			return 2;
		}
		try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(zipStream))
		{
			c = reader.read();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return 3;
		}
		try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world)))
		{
			ClipboardHolder ch = new ClipboardHolder(c);
			ch.setTransform(new AffineTransform().rotateY(rot));
			Operation op = ch.createPaste(session).ignoreAirBlocks(true).copyEntities(includeEntities).to(pos).build();
			Operations.complete(op);
		}
		catch (WorldEditException e)
		{
			e.printStackTrace();
			return 4;
		}
		return 0;
	}
	
	public void CloseDungeon()
	{
		List<Player> allPlayers = world.getPlayers();
		allPlayers.addAll(spectators);
		for (Player p : allPlayers)
		{
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			p.sendMessage(ChatColor.RED + "The Dungeon instance you were in was closed.");
		}
		Bukkit.unloadWorld(world, false);
		world.setAutoSave(false);
		deleteRecursively(world.getWorldFolder());
	}
	
	public void Enter(Player p)
	{
		players.add(p);
		p.teleport(new Location(world, 0.5, 64, 0.5));
		p.sendMessage(ChatColor.GRAY + "You entered the Dungeon.");
		p.sendMessage(ChatColor.GRAY + "Seed: " + seed);
	}
	
	public void Leave(Player p)
	{
		players.remove(p);
		Location l = p.getBedSpawnLocation();
		if(l != null)
			p.teleport(l);
		else
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		if(players.size() == 0)
			CloseDungeon();
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public HashMap<Player, Dungeoneer> getSavedDungeoneers()
	{
		return savedDungeoneers;
	}
	
	public static void deleteRecursively(File directory)
	{
		for (File file : Objects.requireNonNull(directory.listFiles()))
		{
			if (file.isDirectory())
			{
				deleteRecursively(file);
			} else
			{
				file.delete();
			}
		}
		directory.delete();
	}
	
	public enum Type
	{
		Nature("forest", new Range(0, 24000), false, false),
		Desert("desert", new Range(0, 24000), false, false),
		Castle("the_void", new Range(18000, 18000), false, false),
		Ashen("basalt_deltas", new Range(0, 24000), true, false),
		Demonic("crimson_forest", new Range(18000, 18000), true, false),
		Mansion("dark_forest", new Range(12500, 23500), true, true);
		
		public String biome;
		public Range time;
		public boolean storm;
		public boolean thunder;
		Type(String biome, Range time, boolean storm, boolean thunder)
		{
			this.biome = biome;
			this.time = time;
			this.storm = storm;
			this.thunder = thunder;
		}
	}
}
