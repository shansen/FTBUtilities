package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdDelHome extends CmdBase
{
	public CmdDelHome()
	{
		super("delhome", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(sender)).homes.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(sender));

		if (args.length == 0)
		{
			args = new String[] {"home"};
		}

		args[0] = args[0].toLowerCase();

		if (data.homes.set(args[0], null))
		{
			FTBULang.HOME_DEL.sendMessage(sender, args[0]);
		}
		else
		{
			throw FTBULang.HOME_NOT_SET.commandError(args[0]);
		}
	}
}