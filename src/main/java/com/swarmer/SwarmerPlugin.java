package com.swarmer;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.swarmer.overlays.SwarmerOverlay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.image.BufferedImage;
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

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(swarmerOverlay);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();
		final int npcId = npc.getId();
		if (isKephriDowned && npcId == SWARM_NPC_ID)
		{
			SwarmerNpc swarmer = new SwarmerNpc(npc);
			if (swarmers.stream().noneMatch(s -> s.getIndex() == swarmer.getIndex()))
			{
				swarmers.add(swarmer);
				getCardinalNpcs(npc).forEach(cardinalNpc -> {
					SwarmerNpc cardinalSwarmer = new SwarmerNpc(cardinalNpc);
					if (swarmers.stream().noneMatch(s -> s.getIndex() == cardinalSwarmer.getIndex())) {
						swarmers.add(cardinalSwarmer);
					}
				});
				SwarmerPlugin.WaveNumber++;
			}
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

	private List<NPC> getCardinalNpcs(NPC npc)
	{
		WorldPoint npcLocation = npc.getWorldLocation();
		int npcX = npcLocation.getX();
		int npcY = npcLocation.getY();
		WorldPoint north = new WorldPoint(npcX, npcY - 1, client.getPlane());
		WorldPoint south = new WorldPoint(npcX, npcY + 1, client.getPlane());
		WorldPoint east = new WorldPoint(npcX + 1, npcY, client.getPlane());
		WorldPoint west = new WorldPoint(npcX - 1, npcY, client.getPlane());

		NPC northNpc = getNpcOnTile(north);
		NPC southNpc = getNpcOnTile(south);
		NPC eastNpc = getNpcOnTile(east);
		NPC westNpc = getNpcOnTile(west);

		List<NPC> cardinalNpcs = new ArrayList<NPC>();
		if (northNpc != null)
			cardinalNpcs.add(northNpc);
		if (southNpc != null)
			cardinalNpcs.add(southNpc);
		if (eastNpc != null)
			cardinalNpcs.add(eastNpc);
		if (westNpc != null)
			cardinalNpcs.add(westNpc);

		return cardinalNpcs;
	}

	private NPC getNpcOnTile(WorldPoint worldPoint)
	{
		List<NPC> npcs = client.getNpcs();

		for (NPC npc : npcs) {
			if (
				npc.getId() == SWARM_NPC_ID &&
				npc.getWorldLocation().getX() == worldPoint.getX() && npc.getWorldLocation().getY() == worldPoint.getY()
			) {
				return npc;
			}
		}
		return null;
	}

}
