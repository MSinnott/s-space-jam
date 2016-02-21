public class HumanReadable {

    public static String memNumToReadable(float num){
        int mag = 0;
        while(num >= 1024) {num /= 1024; mag++;}
        String ret = String.valueOf(num);
        return ((ret.length() > 4) ?  ret.substring(0, 4) : ret) + ((mag == 0) ? "" : "KMGT".charAt(mag - 1) + "i") + "B";
    }

    public static String neatenFloat(float f){
        String flt  = Float.toString(f);
        System.out.println(flt);
        String ret = "";
        if(Math.abs(f) < 2 * Float.MIN_VALUE) return "0.0";
        if(flt.contains("e")){
            for (int i = 0; i < 4 && i < flt.indexOf("e"); i++) {
                ret += flt.charAt(i);
            }
            ret += flt.substring(flt.indexOf("e"));
        } else {
            ret += flt.charAt(0) + ".";
            for (int i = 1; i < 4 && i < flt.length(); i++) {
                if(flt.charAt(i) != '.') ret += flt.charAt(i);
            }
            ret += "e" + (int) Math.log10(f);
        }
        System.out.println("into " + ret);
        return ret;
    }

}
