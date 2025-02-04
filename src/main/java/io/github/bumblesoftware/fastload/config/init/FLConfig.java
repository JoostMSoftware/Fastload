package io.github.bumblesoftware.fastload.config.init;

import io.github.bumblesoftware.fastload.extensions.SimpleVec2i;
import io.github.bumblesoftware.fastload.init.FastLoad;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import static io.github.bumblesoftware.fastload.config.init.DefaultConfig.*;

@SuppressWarnings("DanglingJavadoc")
public class FLConfig {
    public static void loadClass() {}

    //Init Vars
    private static final Properties properties;
    private static final Properties newProperties;
    private static final Path path;

    //Config Variables

    /**
     * Just parses everything in default as specified in Default config, this package
     */
    protected static int getChunkTryLimit() {
        return getInt(DefaultConfig.propertyKeys.tryLimit(), getTryLimit(), getTryLimitBound());
    }
    protected static int getRawChunkPregenRadius() {
        return getInt(DefaultConfig.propertyKeys.pregen(), getPregenRadius(), getRawRadiusBound());
    }
    protected static int getRawPreRenderRadius() {
        return getInt(propertyKeys.render(), getRenderRadius(), getRawRadiusBound());
    }
    protected static boolean getCloseLoadingScreenUnsafely() {
        return getBoolean(DefaultConfig.propertyKeys.unsafeClose(), getCloseUnsafely());
    }
    protected static boolean getRawDebug() {
        return getBoolean(DefaultConfig.propertyKeys.debug(), getDebug());
    }

    static {
        properties = new Properties();
        newProperties = new Properties();
        path = FabricLoader.getInstance().getConfigDir().resolve(FastLoad.NAMESPACE.toLowerCase() + ".properties");

        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path, StandardOpenOption.CREATE)) {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Don't forget that these variables are sorted alphabetically in .properties files!
        /**
         * Return types aren't used here but they are called specifically just to get the default variables ready
         */
        getChunkTryLimit();
        getRawChunkPregenRadius();
        getRawPreRenderRadius();
        getCloseLoadingScreenUnsafely();
        getRawDebug();

        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            newProperties.store(out,  FastLoad.NAMESPACE +  " Configuration File");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /**
         * Writes our documentation for the config
         */
        try (BufferedWriter comment = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            comment.write("\n");
            comment.write("\n# Definitions");
            comment.write("\n# " + writable(DefaultConfig.propertyKeys.tryLimit()) + " = how many times in a row should the same count of loaded chunks be ignored before we cancel pre-rendering");
            comment.write("\n# Min = 1, Max = 1000. Must be a positive Integer");
            comment.write("\n#");
            comment.write("\n# " + writable(DefaultConfig.propertyKeys.unsafeClose()) + " = should skip 'Joining World', and 'Downloading Terrain'. Potentially can result in joining world before chunks are properly loaded");
            comment.write("\n# Enabled = true, Disabled = false");
            comment.write("\n#");
            comment.write("\n# " + writable(DefaultConfig.propertyKeys.debug()) + " = debug (log) all things happening in fastload to aid in diagnosing issues.");
            comment.write("\n# Enabled = true, Disabled = false");
            comment.write("\n#");
            comment.write("\n# " + writable(DefaultConfig.propertyKeys.render()) + " = how many chunks are loaded until 'building terrain' is completed. Adjusts with FOV to decide how many chunks are visible");
            comment.write("\n# Min = 0, Max = 32 or your render distance, Whichever is smaller. Set 0 to disable. Must be a positive Integer");
            comment.write("\n#");
            comment.write("\n# " + writable(DefaultConfig.propertyKeys.pregen()) + " = how many chunks (from 441 Loading) are pre-generated until the server starts");
            comment.write("\n# Min = 0, Max = 32. Set 0 to only pregen 1 chunk. Must be a positive Integer");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Just parses and ensures values are doing their job
     */
    private static void logError(String key) {
        FastLoad.LOGGER.error("Failed to parse variable '" + key + "' in " + FastLoad.NAMESPACE + "'s config, generating a new one!");
    }
    private static String writable(String key) {
        return "'" + key.toLowerCase() + "'";
    }
    private static int getInt(String key, int def, SimpleVec2i vec2i) {
        try {
            int i = FLMath.parseMinMax(Integer.parseInt(properties.getProperty(key)), vec2i);
            FLConfig.newProperties.setProperty(key, String.valueOf(i));
            return i;
        } catch (NumberFormatException e) {
            logError(key);
            FLConfig.newProperties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    private static boolean parseBoolean(String string) {
        if (string == null) throw new NumberFormatException("null");
        if (string.trim().equalsIgnoreCase("true")) return true;
        if (string.trim().equalsIgnoreCase("false")) return false;
        throw new NumberFormatException(string);
    }
    private static boolean getBoolean(String key, boolean def) {
        try {
            final boolean b = parseBoolean(FLConfig.properties.getProperty(key));
            FLConfig.newProperties.setProperty(key, String.valueOf(b));
            return b;
        } catch (NumberFormatException e) {
            logError(key);
            FLConfig.newProperties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

    /**
     * This method is used by anything that writes a different variable to the config
     * &#064;Param  key is the key is the address of the property that it's writing to
     * &#064;Param  value is the value it's writing.
     * &#064;param  last is the variable that the last key in a loop calls in order to finally write everything down to the new values
     */
    public static void writeToDisk(String key, String value, boolean last) {
        properties.setProperty(key, value);
        if (last) {
            try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                properties.store(out, FastLoad.NAMESPACE +  " Configuration File");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
