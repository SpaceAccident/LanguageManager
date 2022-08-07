package space.accident.language;


import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import space.accident.language.config.Config;
import space.accident.language.extra.SpaceLang;

import static space.accident.language.BuildConfigKt.*;

@Mod(
		modid = MODID,
		version = VERSION,
		name = MODNAME,
		acceptedMinecraftVersions = "[1.7.10]"
)
public class SpaceAccidentLanguageManager {
	
	@Mod.Instance
	public SpaceAccidentLanguageManager INSTANCE;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SpaceLang.init(event.getModConfigurationDirectory().getParentFile());
		Config.createConfig(event.getModConfigurationDirectory());
	}
	
	@Mod.EventHandler
	public void  onServerAboutToStart(FMLServerAboutToStartEvent event) {
		SpaceLang.saveLang();
	}
}