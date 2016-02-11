import com.sun.org.apache.xpath.internal.SourceTree;

import java.text.DecimalFormat;

public class HumanReadable {

    private static DecimalFormat format  = new DecimalFormat();


    //NOT DONE!!! DO NOT USE ... yet
    public static String numToReadable (long num){
        int z = (63 - Long.numberOfLeadingZeros(num));
        String ret = String.valueOf(num / (1 << z / 10 + 1)) + "00";
        int len = (ret.length() > 2) ? 3 : ret.length();
        return ret.substring(0, 2) + "KMGT".charAt(z / 10 - 1);
    }
}
