import java.awt.*;

public class Theme {

    public static Color[] theme = new Color[] {
            new Color(252, 53, 0),
            new Color(252, 127, 3),
            new Color(255, 201, 8),
            new Color(55, 236, 255),
            new Color(29, 46, 255),
            new Color(255, 255, 255),
            new Color(0, 0, 0)
    };

    public static String[] themeKeys = new String[] {
            "bgColor",
            "fgColor",
            "accColor",
            "llnColor",
            "rlnColor",
            "txtColor",
            "black"
    };

    public static Color getThemeColor(String themeKey){
        int loc = find(themeKey, themeKeys);
        if(loc == -1) return getThemeColor("black");
        return theme[loc];
    }

    public static Color getThemeColor(int colrIndex){
        return theme[colrIndex];
    }

    public static void setThemeColor(String themeKey, Color toSet){
        int loc = find(themeKey, themeKeys);
        if (loc == -1) return;
        theme[loc] = toSet;
    }

    private static int find(String toFind, String[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(toFind)) return i;
        }
        return -1;
    }
}
