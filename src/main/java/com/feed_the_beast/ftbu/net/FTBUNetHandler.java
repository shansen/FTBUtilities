package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUNetHandler
{
	static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID);
	static final NetworkWrapper BADGES = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_badges");
	static final NetworkWrapper CLAIMS = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_claims");
	static final NetworkWrapper NBTEDIT = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_nbtedit");
	static final NetworkWrapper VIEW_CRASH = NetworkWrapper.newWrapper(FTBUFinals.MOD_ID + "_view_crash");

	public static void init()
	{
		GENERAL.register(1, new MessageServerInfo());
		GENERAL.register(2, new MessageSendWarpList());
		GENERAL.register(3, new MessageRequestServerInfo());

		BADGES.register(1, new MessageRequestBadge());
		BADGES.register(2, new MessageSendBadge());

		CLAIMS.register(1, new MessageOpenClaimedChunksGui());
		CLAIMS.register(2, new MessageClaimedChunksRequest());
		CLAIMS.register(3, new MessageClaimedChunksUpdate());
		CLAIMS.register(4, new MessageClaimedChunksModify());

		NBTEDIT.register(1, new MessageEditNBT());
		NBTEDIT.register(2, new MessageEditNBTResponse());

		VIEW_CRASH.register(1, new MessageViewCrash());
		VIEW_CRASH.register(2, new MessageViewCrashList());
	}
}