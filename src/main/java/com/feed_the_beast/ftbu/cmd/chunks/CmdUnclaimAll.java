package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.text_components.Notification;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.TextComponentHelper;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdUnclaimAll extends CmdBase
{
	public CmdUnclaimAll()
	{
		super("unclaim_all", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return LIST_TRUE_FALSE;
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		checkArgs(sender, args, 1);

		IForgePlayer p;

		if (args.length >= 2)
		{
			if (!PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
			{
				throw FTBLibLang.COMMAND_PERMISSION.commandError();
			}

			p = getForgePlayer(args[1]);
		}
		else
		{
			p = getForgePlayer(player);
		}

		if (p.getTeam() != null)
		{
			ClaimedChunks.INSTANCE.unclaimAllChunks(p.getTeam(), parseBoolean(args[0]) ? null : player.dimension);
			Notification.of(FTBUFinals.get("unclaimed_all"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.unclaimed_all")).send(player);
		}
	}
}