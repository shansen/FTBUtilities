package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
import com.feed_the_beast.ftbl.lib.util.misc.BroadcastSender;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.util.backups.Backups;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdBackup extends CmdTreeBase
{
	public static class CmdStart extends CmdBase
	{
		public CmdStart()
		{
			super("start", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			boolean b = Backups.INSTANCE.run(server, sender, args.length == 0 ? "" : args[0]);
			if (b)
			{
				FTBULang.BACKUP_MANUAL_LAUNCH.sendMessage(BroadcastSender.INSTANCE, sender.getName());

				if (!FTBUConfig.backups.use_separate_thread)
				{
					Backups.INSTANCE.postBackup();
				}
			}
			else
			{
				FTBULang.BACKUP_ALREADY_RUNNING.sendMessage(sender);
			}
		}
	}

	public static class CmdStop extends CmdBase
	{
		public CmdStop()
		{
			super("stop", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			if (Backups.INSTANCE.thread != null)
			{
				Backups.INSTANCE.thread.interrupt();
				Backups.INSTANCE.thread = null;
				FTBULang.BACKUP_STOP.sendMessage(sender);
				return;
			}

			throw FTBULang.BACKUP_NOT_RUNNING.commandError();
		}
	}

	public static class CmdGetSize extends CmdBase
	{
		public CmdGetSize()
		{
			super("getsize", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			String sizeW = FileUtils.getSizeS(sender.getEntityWorld().getSaveHandler().getWorldDirectory());
			String sizeT = FileUtils.getSizeS(Backups.INSTANCE.backupsFolder);
			FTBULang.BACKUP_SIZE.sendMessage(sender, sizeW, sizeT);
		}
	}

	public CmdBackup()
	{
		super("backup");
		addSubcommand(new CmdStart());
		addSubcommand(new CmdStop());
		addSubcommand(new CmdGetSize());
	}
}