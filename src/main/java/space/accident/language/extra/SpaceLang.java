package space.accident.language.extra;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import space.accident.language.config.Config;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static space.accident.language.config.Config.needSaveAll;

@SuppressWarnings("ALL")
public class SpaceLang {
	
	private static File langFolder;
	
	public static void init(File mc) {
		langFolder = new File(mc, "SpaceAccident/lang");
		langFolder.mkdirs();
	}
	
	public static void saveLang() {
		if (!needSaveAll) {
			try {
				searchDirForLanguages(langFolder, "", FMLCommonHandler.instance().getEffectiveSide());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		Map<String, Properties> langFiles = new HashMap<>();
		try {
			Field map = LanguageRegistry.class.getDeclaredField("modLanguageData");
			map.setAccessible(true);
			langFiles = (Map<String, Properties>) map.get(LanguageRegistry.instance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String lang : Config.langList) {
			Properties prop = langFiles.get(lang);
			if (prop != null) {
				File langFile = new File(langFolder, lang + ".lang");
				try (FileOutputStream stream = new FileOutputStream(langFile);
					 OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
					 BufferedWriter bufferedWriter = new BufferedWriter(writer, 8 * 1024)) {
					prop.store(bufferedWriter, "All lang files from mods");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void searchDirForLanguages(File source, String path, Side side) throws IOException {
		for (File file : source.listFiles()) {
			String currPath = path + file.getName();
			if (file.isDirectory()) {
				searchDirForLanguages(file, currPath + '/', side);
			}
			String lang = file.getName().substring(0, 5);
			LanguageRegistry.instance().injectLanguage(lang, StringTranslate.parseLangFile(new FileInputStream(file)));
			// Ensure en_US is available to StringTranslate on the server
			if ("en_US".equals(lang) && side == Side.SERVER) {
				StringTranslate.inject(new FileInputStream(file));
			}
		}
	}
	
	public static String addStringLocalization(String key, String english) {
		if (key == null) return "";
		String translate = LanguageRegistry.instance().getStringLocalization(key);
		if (!translate.isEmpty()) return translate;
		LanguageRegistry.instance().addStringLocalization(key, "en_US", english);
		return english;
	}
	
	public static String getTranslation(String key) {
		if (key == null) return "";
		String tTrimmedKey = key.trim(), rTranslation = LanguageRegistry.instance().getStringLocalization(tTrimmedKey);
		if (rTranslation == null || rTranslation.length() <= 0) {
			rTranslation = StatCollector.translateToLocal(tTrimmedKey);
			if (rTranslation == null || rTranslation.length() <= 0 || tTrimmedKey.equals(rTranslation)) {
				if (key.endsWith(".name")) {
					String substring = tTrimmedKey.substring(0, tTrimmedKey.length() - 5);
					rTranslation = StatCollector.translateToLocal(substring);
					if (rTranslation == null || rTranslation.length() <= 0 || substring.equals(rTranslation)) {
						return key;
					}
				} else {
					rTranslation = StatCollector.translateToLocal(tTrimmedKey + ".name");
					if (rTranslation == null || rTranslation.length() <= 0 || (tTrimmedKey + ".name").equals(rTranslation)) {
						return key;
					}
				}
			}
		}
		return rTranslation;
	}
	
	public static String getTranslation(String key, String separator) {
		if (key == null) return "";
		StringBuilder rTranslationSB = new StringBuilder();
		for (String tString : key.split(separator)) {
			rTranslationSB.append(getTranslation(tString));
		}
		return rTranslationSB.toString();
	}
}