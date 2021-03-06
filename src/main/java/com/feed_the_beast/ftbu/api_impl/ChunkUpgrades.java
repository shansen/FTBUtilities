package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.RegisterChunkUpgradesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class ChunkUpgrades
{
	public static final ChunkUpgrade LOADED = new ChunkUpgrade("loaded");
	public static final ChunkUpgrade NO_EXPLOSIONS = new ChunkUpgrade("no_explosions", true); //TODO: Make real
	public static final ChunkUpgrade NO_PVP = new ChunkUpgrade("no_pvp", true); //TODO: Make real

	@SubscribeEvent
	public static void addUpgrades(RegisterChunkUpgradesEvent event)
	{
		event.register(LOADED);
		event.register(NO_EXPLOSIONS);
		event.register(NO_PVP);
	}
}