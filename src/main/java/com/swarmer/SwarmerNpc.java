package com.swarmer;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.ArrayUtils;
import com.swarmer.SwarmerPlugin;

import javax.inject.Inject;
import java.awt.*;

@Getter
public class SwarmerNpc
{
    private final NPC npc;

    private final int id;

    private final int index;

    @Setter
    private boolean isAlive;

    private final int waveSpawned;

    public SwarmerNpc(NPC npc)
    {
        this.npc = npc;
        this.id = npc.getId();
        this.index = npc.getIndex();
        this.isAlive = true;
        this.waveSpawned = SwarmerPlugin.WaveNumber;
    }
}