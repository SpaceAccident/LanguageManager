package space.accident.language.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
	
	public static String[] langList;
	public static boolean needSaveAll = false;
	
	
	public static void createConfig(File configFile) {
		File config = new File(new File(configFile, "SpaceAccident"), "LangProperty.cfg");
		Configuration cfg = new Configuration(config);
		
		langList = cfg.get("general", "lang_list", new String[]{"en_US", "ru_RU"},
				"The list of lang files to unload from all mods into one file."
		).getStringList();
		
		needSaveAll = cfg.get("general", "need_save_all", false, "Enabled save all lang files").getBoolean();
		
		cfg.save();
	}
}
