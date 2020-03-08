package xyz.xy718.poster.config;

import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.xy718.poster.XyDashbosrdPosterPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * 本文件来自<br>
 * https://github.com/Jecvay/ESPayMoney
 * @author Xy718
 *
 */
public class I18N {
    private static Logger logger = null;
    private static I18N ins = null;
    private static XyDashbosrdPosterPlugin plugin;
    private static String assetsName = "i18n";

    private ResourceBundle rb;
    private ResourceBundle assetRb;

    private I18N(Locale locale) {
        this.rb = getCustomBundle(assetsName, locale);
        this.assetRb = ResourceBundle.getBundle("assets."+XyDashbosrdPosterPlugin.getPLUGIN_ID()+"." + assetsName, locale);
    }

    static public I18N getInstance() {
        if (ins == null) {
            ins = new I18N(LocaleUtils.toLocale("en_US"));
        }
        return ins;
    }

    private ResourceBundle getCustomBundle(String name, Locale locale) {
        Path configDir = plugin.getConfigDir();
        Path langDir = configDir.resolve("lang");
        if (!Files.exists(langDir)) {
            try {
                Files.createDirectories(langDir);
            } catch (IOException e) {
                logger.error("Failed to create langDir directory: {}", e);
            }
        }
        String fileName;
        if (locale.toString().equals("en_US")) {
            fileName = name + ".properties";
        } else {
            fileName = String.join("_", name, locale.toString()) + ".properties";
        }
        Path filePath = langDir.resolve(fileName);
        if (!Files.exists(filePath)) {
            try {
            	plugin.getContainer().getAsset(fileName).get().copyToFile(filePath);
            } catch (IOException e) {
                logger.error("Failed to copy language file {}", e);
            }
        }

        File file = new File(langDir.toString());
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{file.toURI().toURL()};
        } catch (MalformedURLException e) {
            logger.error("Failed to create resource loader {}", e);
        }
        ClassLoader loader = new URLClassLoader(urls);
        return ResourceBundle.getBundle(name, locale, loader, new UTF8Control());
    }

    /////////////////

    static public void setLocale(Locale locale) {
        logger.info("Set language to {}", locale.toLanguageTag());
        ins = new I18N(locale);
    }

    static public void setPlugin(XyDashbosrdPosterPlugin plugin) {
        I18N.plugin = plugin;
    }

    public static void setLogger(Logger logger) {
        I18N.logger = logger;
    }

    public static String getString(String key, Object... args) {
        if (getInstance().rb.containsKey(key)) {
            String value = getInstance().rb.getString(key);
            return MessageFormat.format(value, args);
        } else if (getInstance().assetRb.containsKey(key)) {
            String value = getInstance().assetRb.getString(key);
            return MessageFormat.format(value, args);
        } else {
            return key;
        }
    }

    public static Text getText(String key, Object... args) {
        String filledValue = getString(key, args);
        if (filledValue.equals(key)) {
            return Text.of(key);
        } else {
            return TextSerializers.FORMATTING_CODE.deserialize(filledValue);
        }
    }
}

class UTF8Control extends ResourceBundle.Control {
    // https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle/4660195#4660195
    public ResourceBundle newBundle
            (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IOException
    {
        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                // Only this line is changed to make it to read properties files as UTF-8.
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}