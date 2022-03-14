package yaya.dungeons.dungeons;

import com.sk89q.worldedit.math.BlockVector3;

public class DungeonRoom
{
	public final int rot;
	public final BlockVector3 pos;
	public final BlockVector3 offset;
	public final String roomType;
	
	public DungeonRoom(int rot, BlockVector3 pos, BlockVector3 offset)
	{
		this(rot, pos, offset, "Room");
	}
	
	public DungeonRoom(int rot, BlockVector3 pos, BlockVector3 offset, String roomType)
	{
		this.rot = rot;
		this.pos = pos;
		this.offset = offset;
		this.roomType = roomType;
	}
}
