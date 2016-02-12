public class HumanReadable {

    public static String numToReadable (float num){
        int mag = 0;
        while(num >= 1024) {num /= 1024; mag++;}
        String ret = String.valueOf(num);
        return ((ret.length() > 4) ?  ret.substring(0, 4) : ret) + ((mag == 0) ? "" : "KMGT".charAt(mag - 1) + "i") + "B";
    }
}
