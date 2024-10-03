package com.swarmer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup(SwarmerConfig.GROUP)
public interface SwarmerConfig extends Config
{
	String GROUP = "swarmer";

	@ConfigSection(
		name = "Color Settings",
		position = 0,
		description = "Text settings",
		closedByDefault = false
	)
	String colorSettings = "colorSettings";

	@ConfigItem(
		position = 0,
		keyName = "color",
		name = "Color",
		description = "Color of the text",
		section = colorSettings
	)
	default Color color()
	{
		return Color.WHITE;
	}

	@ConfigItem(
			position = 1,
			keyName = "outlineColor",
			name = "Outline Color",
			description = "Outline color of the text",
			section = colorSettings
	)
	default Color outlineColor()
	{
		return Color.BLACK;
	}

	@ConfigItem(
			position = 2,
			keyName = "drawOutline",
			name = "Draw outline",
			description = "Draws an outline around the text",
			section = colorSettings
	)
	default boolean drawOutline()
	{
		return true;
	}

	// ------------------------------------------------------------

	@ConfigSection(
			name = "Font Settings",
			position = 1,
			description = "Fonts settings",
			closedByDefault = true
	)
	String fontsSettings = "fontsSettings";

	@ConfigItem(
			position = 0,
			keyName = "fontType",
			name = "Font Type",
			description = "Type of the font",
			section = fontsSettings
	)
	default SwarmerFonts fontType()
	{
		return SwarmerFonts.ARIAL;
	}

	@ConfigItem(
			position = 1,
			keyName = "fontStyle",
			name = "Bold Font",
			description = "Makes the font bold",
			section = fontsSettings
	)
	default boolean fontBold()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "fontSize",
			name = "Font Size",
			description = "Size of the font",
			section = fontsSettings
	)
	default int fontSize()
	{
		return 16;
	}

}
