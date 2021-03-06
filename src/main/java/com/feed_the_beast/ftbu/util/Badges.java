package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class Badges
{
	public static final String MAIN_ADDRESS = "badges.latmod.com";
	public static final int MAIN_PORT = 25566;
	private static final String FALLBACK_URL = "http://badges.latmod.com/get?id=";

	public static final Map<UUID, String> BADGE_CACHE = new HashMap<>();
	public static final Map<UUID, String> LOCAL_BADGES = new HashMap<>();

	public static void update(UUID playerId)
	{
		BADGE_CACHE.remove(playerId);
	}

	public static String get(UUID playerId)
	{
		String badge = BADGE_CACHE.get(playerId);

		if (badge != null)
		{
			return badge;
		}

		badge = getRaw(playerId);
		BADGE_CACHE.put(playerId, badge);
		return badge;
	}

	private static String getRaw(UUID playerId)
	{
		IForgePlayer player = FTBLibAPI.API.getUniverse().getPlayer(playerId);

		if (player == null || player.isFake())
		{
			return "";
		}

		FTBUPlayerData data = FTBUPlayerData.get(player);

		if (!data.renderBadge.getBoolean())
		{
			return "";
		}
		else if (FTBUConfig.login.enable_global_badges && !data.disableGlobalBadge.getBoolean())
		{
			Socket socket = null;
			DataOutputStream output = null;
			DataInputStream input = null;

			try
			{
				socket = new Socket(MAIN_ADDRESS, MAIN_PORT);
				output = new DataOutputStream(socket.getOutputStream());
				output.writeLong(playerId.getMostSignificantBits());
				output.writeLong(playerId.getLeastSignificantBits());
				output.flush();
				input = new DataInputStream(socket.getInputStream());
				String badge = input.readUTF();
				boolean event = input.readBoolean();

				if (!badge.isEmpty() && (FTBUConfig.login.enable_event_badges || !event))
				{
					return badge;
				}

				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
				IOUtils.closeQuietly(socket);
			}
			catch (Exception ex)
			{
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
				IOUtils.closeQuietly(socket);

				FTBUFinals.LOGGER.warn("Main Badge API errored, using fallback: " + ex);

				try
				{
					HttpURLConnection connection = (HttpURLConnection) new URL(FALLBACK_URL + StringUtils.fromUUID(playerId)).openConnection();
					String badge = StringUtils.readString(connection.getInputStream());

					if (!badge.isEmpty())
					{
						return badge;
					}
				}
				catch (Exception ex2)
				{
					FTBUFinals.LOGGER.warn("Fallback Badge API errored: " + ex2);
				}
			}
		}

		String badge = LOCAL_BADGES.get(playerId);
		return (badge == null || badge.isEmpty()) ? FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.BADGE).getString() : badge;
	}

	public static boolean reloadServerBadges()
	{
		try
		{
			BADGE_CACHE.clear();
			LOCAL_BADGES.clear();
			File file = new File(CommonUtils.folderLocal, "ftbutilities/server_badges.json");

			if (!file.exists())
			{
				JsonObject o = new JsonObject();
				o.addProperty("uuid", "url_to.png");
				o.addProperty("username", "url2_to.png");
				JsonUtils.toJson(o, file);
			}
			else
			{
				for (Map.Entry<String, JsonElement> entry : JsonUtils.fromJson(file).getAsJsonObject().entrySet())
				{
					IForgePlayer player = FTBLibAPI.API.getUniverse().getPlayer(entry.getKey());

					if (player != null)
					{
						LOCAL_BADGES.put(player.getId(), entry.getValue().getAsString());
					}
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}