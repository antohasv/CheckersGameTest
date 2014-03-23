package utils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Logger;

import base.Frontend;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateHelper {
	private static Configuration cfg = new Configuration();
	public static void init() {
		try {
			cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/static/html"));				
		} catch (IOException e) {
			e.printStackTrace();
			Logger logger = Logger.getLogger(Frontend.class.toString());
			logger.info("Can't find folder with templates. Shutting down...");
			System.exit(404);
		}
	}

	public static void renderTemplate(String name, Map<String, String> map, Writer out) {
		Template template;
		try {
			template = cfg.getTemplate(name);
			template.process(map, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}