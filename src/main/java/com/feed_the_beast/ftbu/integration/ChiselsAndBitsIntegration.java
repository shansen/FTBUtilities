package com.feed_the_beast.ftbu.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import mod.chiselsandbits.api.EventBlockBitModification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = FTBLibFinals.CHISELS_AND_BITS)
public class ChiselsAndBitsIntegration
{
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChiselEvent(EventBlockBitModification event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), event.getHand(), new BlockPosContainer(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos())), event.isPlacing() ? BlockInteractionType.CNB_PLACE : BlockInteractionType.CNB_BREAK))
		{
			event.setCanceled(true);
		}
	}
}