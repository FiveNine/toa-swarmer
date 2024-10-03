package com.swarmer;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.swarmer.overlays.SwarmerOverlay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Swarmer"
)
public class SwarmerPlugin extends Plugin
{
	@Inject
	public Client client;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SwarmerOverlay swarmerOverlay;

	public static final int SWARM_NPC_ID = 11723;
	public static int WaveNumber = 1;

	private final int KEPHRI_DOWNED_NPC_ID = 11720;
	private final int[] KEPHRI_ALIVE_NPC_IDS = {11721, 11719};

	private boolean isKephriDowned = false;

	@Getter
	ArrayList<SwarmerNpc> swarmers = new ArrayList<SwarmerNpc>();

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(swarmerOverlay);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();
		final int npcId = npc.getId();
		if (isKephriDowned && npcId == SWARM_NPC_ID)
		{
			SwarmerNpc swarmer = new SwarmerNpc(npc);
			SwarmerPlugin.WaveNumber++;
			swarmers.add(swarmer);
		}
		else if (Arrays.stream(KEPHRI_ALIVE_NPC_IDS).anyMatch(id -> id == npcId))
		{
			isKephriDowned = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		List<NPC> npcs = client.getNpcs();
		for (NPC npc : npcs)
		{
			if (!isKephriDowned && npc.getId() == KEPHRI_DOWNED_NPC_ID)
			{
				isKephriDowned = true;
				SwarmerPlugin.WaveNumber = 1;
			}
			else if (isKephriDowned && ArrayUtils.contains(KEPHRI_ALIVE_NPC_IDS, npc.getId()))
			{
				isKephriDowned = false;
			}
		}

		for (int i = swarmers.size() - 1; i >= 0; i--)
		{
			SwarmerNpc swarmer = swarmers.get(i);
			if (!npcs.contains(swarmer.getNpc()))
			{
				swarmers.remove(i);
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			isKephriDowned = false;
			SwarmerPlugin.WaveNumber = 1;
			swarmers.clear();
		}
	}

	@Provides
	SwarmerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SwarmerConfig.class);
	}
}
