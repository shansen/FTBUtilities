package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.RegisterCustomPermissionPrefixesEvent;
import com.feed_the_beast.ftbu.api.chunks.RegisterChunkUpgradesEvent;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import com.feed_the_beast.ftbu.ranks.FTBUPermissionHandler;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class FTBUCommon
{
	public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();

	public void preInit()
	{
		FTBUConfig.sync();

		File oldConfig = new File(CommonUtils.folderLocal, "ftbu");

		if (oldConfig.exists())
		{
			FileUtils.delete(oldConfig);
		}

		if (FTBUConfig.ranks.enabled)
		{
			PermissionAPI.setPermissionHandler(FTBUPermissionHandler.INSTANCE);
		}

		FTBUtilitiesAPI.API = new FTBUtilitiesAPI_Impl();
		FTBUNetHandler.init();

		if (!ForgeChunkManager.getConfig().hasCategory(FTBUFinals.MOD_ID))
		{
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumChunksPerTicket", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().get(FTBUFinals.MOD_ID, "maximumTicketCount", 1000000).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}

		ForgeChunkManager.setForcedChunkLoadingCallback(FTBU.INST, ClaimedChunks.INSTANCE);

		new RegisterCustomPermissionPrefixesEvent(CUSTOM_PERM_PREFIX_REGISTRY::add).post();
	}

	public void init()
	{
	}

	public void postInit()
	{
		new RegisterChunkUpgradesEvent(upgrade -> FTBUUniverseData.CHUNK_UPGRADES.put(upgrade.getName(), upgrade)).post();
	}
}