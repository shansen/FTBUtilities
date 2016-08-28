package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandSubBase;
import com.feed_the_beast.ftbl.api.item.LMInvUtils;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.latmod.lib.util.LMNBTUtils;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class CmdInv extends CommandSubBase
{
    public static class CmdView extends CommandLM
    {
        public CmdView()
        {
            super("view");
        }

        @Override
        public String getCommandUsage(ICommandSender ics)
        {
            return '/' + commandName + " <player>";
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
            EntityPlayerMP ep = getPlayer(server, ics, args[0]);
            ep0.displayGUIChest(new InvSeeInventory(ep));
        }
    }

    public static class CmdSave extends CommandLM
    {
        public CmdSave()
        {
            super("save");
        }

        @Override
        public String getCommandUsage(ICommandSender ics)
        {
            return '/' + commandName + " <player> <file_id>";
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            checkArgs(args, 2, "<player> <ID>");
            EntityPlayerMP ep = getPlayer(server, ics, args[0]);
            File file = new File(FTBLib.folderLocal, "ftbu/playerinvs/" + LMStringUtils.fromUUID(ep.getGameProfile().getId()) + "_" + args[1].toLowerCase() + ".dat");

            try
            {
                NBTTagCompound tag = new NBTTagCompound();
                writeItemsToNBT(ep.inventory, tag, "Inventory");

                if(Loader.isModLoaded("Baubles"))
                {
                    writeItemsToNBT(LMInvUtils.getBaubles(ep), tag, "Baubles");
                }

                LMNBTUtils.writeTag(file, tag);
            }
            catch(Exception e)
            {
                if(FTBLib.DEV_ENV)
                {
                    e.printStackTrace();
                }
                throw FTBLibLang.RAW.commandError("Failed to load inventory!");
            }
        }

        private static void writeItemsToNBT(IInventory inv, NBTTagCompound compound, String s)
        {
            NBTTagList nbttaglist = new NBTTagList();

            for(int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack is = inv.getStackInSlot(i);

                if(is != null)
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte) i);
                    is.writeToNBT(nbttagcompound);
                    nbttaglist.appendTag(nbttagcompound);
                }
            }

            compound.setTag(s, nbttaglist);
        }
    }

    public static class CmdLoad extends CommandLM
    {
        public CmdLoad()
        {
            super("load");
        }

        @Override
        public String getCommandUsage(ICommandSender ics)
        {
            return '/' + commandName + " <player> <file_id>";
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            checkArgs(args, 2, "<player> <ID>");
            EntityPlayerMP ep = getPlayer(server, ics, args[0]);
            File file = new File(FTBLib.folderLocal, "ftbu/playerinvs/" + LMStringUtils.fromUUID(ep.getGameProfile().getId()) + "_" + args[1].toLowerCase() + ".dat");

            try
            {
                NBTTagCompound tag = LMNBTUtils.readTag(file);

                readItemsFromNBT(ep.inventory, tag, "Inventory");

                if(Loader.isModLoaded("Baubles"))
                {
                    readItemsFromNBT(LMInvUtils.getBaubles(ep), tag, "Baubles");
                }
            }
            catch(Exception e)
            {
                if(FTBLib.DEV_ENV)
                {
                    e.printStackTrace();
                }
                throw FTBLibLang.RAW.commandError("Failed to load inventory!");
            }
        }

        private static void readItemsFromNBT(IInventory inv, NBTTagCompound compound, String s)
        {
            NBTTagList nbttaglist = compound.getTagList(s, 10);

            for(int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;

                if(j >= 0 && j < inv.getSizeInventory())
                {
                    inv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
                }
            }
        }
    }

    public static class CmdList extends CommandLM
    {
        public CmdList()
        {
            super("list");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
        }
    }

    public CmdInv()
    {
        super("inv");
        add(new CmdView());
        add(new CmdSave());
        add(new CmdLoad());
        add(new CmdList());
    }
}