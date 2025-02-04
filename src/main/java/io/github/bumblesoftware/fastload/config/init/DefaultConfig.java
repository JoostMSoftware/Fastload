package io.github.bumblesoftware.fastload.config.init;


import io.github.bumblesoftware.fastload.extensions.SimpleVec2i;

public class DefaultConfig {
    //Get Strings

    /**
     * &#064; This class is here as the default values for the property keys, it's all centralised here
     * as to make it easy to change things, because hardcoding = bad.
     */
    public static class propertyKeys {
        public static String pregen() {
            return pregen(true) + "_radius";
        }
        public static String pregen(boolean raw) {
            if (raw) {
                return "pregen_chunk";
            } else return pregen();
        }
        public static String render() {
            return render(true) + "_radius";
        }
        public static String render(boolean raw) {
            if (raw) {
                return "pre_render";
            } else return render();
        }
        public static String tryLimit() {
            return "chunk_try_limit";
        }
        public static String unsafeClose() {
            return "close_loading_screen_unsafely";
        }
        public static String debug() {
            return "debug";
        }

    }

    /**
     * Bounds used by fastload in order to avoid crashes or other weird bugs.
     */

    //Get Bounds
    protected static SimpleVec2i getRawRadiusBound() {
        return new SimpleVec2i(32, 0);
    }
    protected static SimpleVec2i getTryLimitBound() {
        return new SimpleVec2i(1000, 1);
    }
    /**
     * DEFAULT, NOT PERMANENT; Constants used by fastload.
     */

    //Get Vars
    protected static int getRenderRadius() {
        return 0;
    }
    protected static int getPregenRadius() {
        return 5;
    }
    protected static int getTryLimit() {
        return 100;
    }
    protected static boolean getCloseUnsafely() {
        return false;
    }
    protected static boolean getDebug() {
        return false;
    }
}
