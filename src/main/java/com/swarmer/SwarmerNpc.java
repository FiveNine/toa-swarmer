package com.swarmer;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.ArrayUtils;
import com.swarmer.SwarmerPlugin;

import javax.inject.Inject;
import java.awt.*;

public class SwarmerNpc
{
    @Getter
    private NPC npc;

    @Getter
    private int id;

    @Getter
    private int index;

    @Getter
    @Setter
    private boolean isAlive;

    @Getter
    private int waveSpawned;


    public SwarmerNpc(NPC npc)
    {
        this.npc = npc;
        this.id = npc.getId();
        this.index = npc.getIndex();
        this.isAlive = true;
        this.waveSpawned = SwarmerPlugin.WaveNumber;
    }
}