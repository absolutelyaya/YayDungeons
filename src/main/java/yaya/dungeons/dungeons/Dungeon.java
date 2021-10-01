package yaya.dungeons.dungeons;

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
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Dungeon
{
	private UUID id;
	private int sizeX;
	private int sizeY;
	
	World world;
	List<Player> players = new ArrayList<>();
	List<Player> spectators = new ArrayList<>();
	
	public Dungeon(UUID id, int sizeX, int sizeY)
	{
		this.id = id;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		generate();
	}
	
	void generate()
	{
		Bukkit.broadcastMessage("Generating a dungeon...");
		WorldCreator wc = new WorldCreator("Dungeon-" + id.toString());
		wc.type(WorldType.FLAT);
		wc.generatorSettings("{\"structures\": {\"structures\": {\"village\": {\"salt\": 8015723, \"spacing\": 32, \"separation\": 8}}}, " +
									 "\"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\": \"the_void\"}");
		wc.generateStructures(false);
		world = wc.createWorld();
		if(placeRoom("Rooms/NatureEntrance.schem", BlockVector3.at(0,63,0)))
			Bukkit.broadcastMessage("Dungeon Generation Complete!");
		else
		{
			Bukkit.broadcastMessage("Something went wrong during Dungeon generation.");
			world.getBlockAt(0, 63, 0).setType(Material.GLASS);
		}
	}
	
	boolean placeRoom(String room, BlockVector3 pos)
	{
		Clipboard c;
		GZIPInputStream zipStream = null;
		try
		{
			File tmp = File.createTempFile("tmp", "schem");
			GZIPOutputStream zipOut = new GZIPOutputStream(new FileOutputStream(tmp));
			InputStream inStream = getClass().getClassLoader().getResourceAsStream(room);
			int b;
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
			return false;
		}
		try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(zipStream))
		{
			c = reader.read();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world)))
		{
			Operation op = new ClipboardHolder(c).createPaste(session).ignoreAirBlocks(true).to(pos).build();
			Operations.complete(op);
		}
		catch (WorldEditException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
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
		deleteRecursively(world.getWorldFolder());
	}
	
	public void Enter(Player p)
	{
		players.add(p);
		p.teleport(new Location(world, 0.5, 64, 0.5));
		p.sendMessage(ChatColor.GRAY + "You entered the Dungeon.");
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
}
