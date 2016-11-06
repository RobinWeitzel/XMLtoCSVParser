package xmltocsvparser.model;

import xmltocsvparser.MainApp;

import java.util.prefs.Preferences;

/**
 * Created by Robin on 06.11.2016.
 */
public class SettingsHandler {
    private static Preferences prefs = Preferences.userNodeForPackage(MainApp.class);

    public static String getSeperationCharacter() {
        String seperationCharacter = prefs.get("seperationCharacter", null);

        if (seperationCharacter != null) {
            return seperationCharacter;
        } else {
            return ";";
        }
    }

    public static void setSeperationCharacter(String seperationCharacter) {
        prefs.put("seperationCharacter", seperationCharacter);

    }

    public static boolean getUseExcelLayout() {
        String useExcelLayout = prefs.get("useExcelLayout", null);

        if (useExcelLayout != null) {
            return Boolean.valueOf(useExcelLayout);
        } else {
            return true;
        }
    }

    public static void setUseExcelLayout(boolean useExcelLayout) {
        prefs.put("useExcelLayout", Boolean.toString(useExcelLayout));

    }

    public static String getMatchingMethod() {
        String matchingMethod = prefs.get("matchingMethod", null);

        if (matchingMethod != null) {
            return matchingMethod;
        } else {
            return "manuell";
        }
    }

    public static void setMatchingMethod(String matchingMethod) {
        prefs.put("matchingMethod", matchingMethod);

    }

    public static boolean getUseWeakPaths() {
        String useWeakPaths = prefs.get("useWeakPaths", null);

        if (useWeakPaths != null) {
            return Boolean.valueOf(useWeakPaths);
        } else {
            return true;
        }
    }

    public static void setUseWeakPaths(boolean useWeakPaths) {
        prefs.put("useWeakPaths", Boolean.toString(useWeakPaths));

    }

    public static int getThreadCount() {
        String threadCount = prefs.get("threadCount", null);

        if (threadCount != null) {
            return Integer.parseInt(threadCount);
        } else {
            return 8;
        }
    }

    public static void setThreadCount(int threadCount) {
        prefs.put("threadCount", Integer.toString(threadCount));
    }

    public static boolean getFirstTime() {
        String firstTime = prefs.get("firstTime", null);

        if (firstTime != null) {
            return Boolean.valueOf(firstTime);
        } else {
            return true;
        }
    }

    public static void setFirstTime(boolean firstTime) {
        prefs.put("firstTime", Boolean.toString(firstTime));
    }

    public static double getThreshold() {
        String threshold = prefs.get("threshold", null);

        if (threshold != null) {
            return Double.parseDouble(threshold);
        } else {
            return 0.5;
        }
    }

    public static void setThreshold(double threshold) {
        prefs.put("threshold", Double.toString(threshold));
    }

    public static void resetPrefs() {
        prefs.remove("seperationCharacter");
        prefs.remove("useExcelLayout");
        prefs.remove("matchingMethod");
        prefs.remove("useWeakPaths");
        prefs.remove("threadCount");
        prefs.remove("firstTime");
        prefs.remove("threshold");
    }
}
