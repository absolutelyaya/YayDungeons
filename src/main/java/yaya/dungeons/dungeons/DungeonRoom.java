package yaya.dungeons.dungeons;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoom
{
	public final int rot, depth;
	public final BlockVector3 pos;
	public final BlockVector3 offset;
	public final String roomType;
	public final boolean includeEntities;
	public final DungeonRoom parent;
	public final List<DungeonRoom> children = new ArrayList<>();
	public final List<DungeonRoom> removedChildren = new ArrayList<>();
	public final List<String> attemptedSchems = new ArrayList<>();
	
	public int generationAttempts;
	
	private EditSession session;
	
	public DungeonRoom(DungeonRoom parent, int rot, BlockVector3 pos, BlockVector3 offset, boolean includeEntities)
	{
		this(parent, rot, pos, offset, includeEntities, "Room");
	}
	
	public DungeonRoom(DungeonRoom parent, int rot, BlockVector3 pos, BlockVector3 offset, boolean includeEntities, String roomType)
	{
		this.parent = parent;
		this.rot = rot;
		this.pos = pos;
		this.offset = offset;
		this.roomType = roomType;
		this.includeEntities = includeEntities;
		if(parent != null)
		{
			parent.addChild(this);
			depth = parent.depth + 1;
		}
		else
			depth = 0;
	}
	
	public void addChild(DungeonRoom child)
	{
		children.add(child);
	}
	
	public void remove(Dungeon owner)
	{
		for (DungeonRoom r : children)
		{
			r.remove(owner);
		}
		for (DungeonRoom r : removedChildren)
		{
			children.remove(r);
		}
		removedChildren.clear();
		
		if(session != null)
		{
			session.undo(session);
			session.close();
		}
		parent.removedChildren.add(this);
		owner.removeRoom(this);
		//System.out.println("room removed");
	}
	
	public void close()
	{
		for (DungeonRoom child : children)
		{
			child.close();
		}
		if(session != null)
			session.close();
	}
	
	public void setSession(EditSession session)
	{
		this.session = session;
	}
}
