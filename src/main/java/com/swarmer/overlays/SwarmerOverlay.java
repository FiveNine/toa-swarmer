package com.swarmer.overlays;

import com.google.common.collect.ArrayListMultimap;
import com.swarmer.SwarmerConfig;
import com.swarmer.SwarmerNpc;
import com.swarmer.SwarmerPlugin;

import java.awt.*;
import java.util.stream.IntStream;
import javax.inject.Inject;

import net.runelite.api.FontTypeFace;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.FontType;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;


public class SwarmerOverlay extends Overlay
{
    public ArrayListMultimap<WorldPoint, SwarmerNpc> swarmers;
    private final SwarmerConfig config;
    private final SwarmerPlugin plugin;
    private final NpcUtil npcUtil;

    @Inject
    private SwarmerOverlay(SwarmerPlugin plugin, SwarmerConfig config, NpcUtil npcUtil)
    {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        this.setLayer(OverlayLayer.UNDER_WIDGETS);
        this.npcUtil = npcUtil;
        this.plugin = plugin;
        this.config = config;
        this.swarmers = ArrayListMultimap.create();
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        this.swarmers.clear();
        swarmers = ArrayListMultimap.create();
        int length = this.plugin.getSwarmers().size();

        for (int i = length - 1; i >= 0; i--)
        {
            SwarmerNpc swarmer = this.plugin.getSwarmers().get(i);
            if (swarmer.isAlive() && !npcUtil.isDying(swarmer.getNpc()))
            {
                swarmers.put(swarmer.getNpc().getWorldLocation(), swarmer);
            }
        }
        if (!swarmers.isEmpty())
        {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            swarmers.asMap().forEach(
                (worldPoint, npcs) -> {
                    int offset = 0;
                    for (SwarmerNpc swarmer : npcs)
                    {
                        this.draw(graphics, swarmer, offset);
                        offset += graphics.getFontMetrics().getHeight();
                    }
                });
        }
        return null;
    }

    private void draw(Graphics2D graphics, SwarmerNpc swarmer, int offset)
    {
        String text = String.valueOf(swarmer.getWaveSpawned());

        Point canvasTextLocation = swarmer.getNpc().getCanvasTextLocation(graphics, text, 0);
        if (canvasTextLocation == null)
        {
            return;
        }
        int x = canvasTextLocation.getX();
        int y = canvasTextLocation.getY() + offset;

        graphics.setFont(new Font(config.fontType().toString(), config.fontBold() ? Font.BOLD : Font.PLAIN, config.fontSize()));

        if (config.drawOutline())
        {
            graphics.setColor(config.outlineColor());
            IntStream.range(-1, 2).forEachOrdered(ex -> {
                IntStream.range(-1, 2).forEachOrdered(ey -> {
                    if (ex != 0 && ey != 0)
                    {
                        graphics.drawString(text, x + ex, y + ey);
                    }
                });
            });
        }

        Color color = config.color();
        graphics.setColor(color);

        graphics.drawString(text, x, y);
    }
}
