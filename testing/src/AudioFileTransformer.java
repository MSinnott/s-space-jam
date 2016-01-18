/**
 * Created by abhijit on 1/17/16.
 */
/*Class that handles the transformation of the byte array to s-space from t-space
*
* Work in Progress.... we have a FFT class --m
* */
public class AudioFileTransformer {
    /*For Reference this is how the DFT works*/
    public static String findDFT(double[] realIn, double[] imaginaryIn, double[] realOut, double[] imaginaryOut) {
        int n = realIn.length;
        for (int a = 0; a < n; a++) {
            double summationReal = 0;
            double summationImaginary = 0;
            for(int z = 0; z < n; z++) {
                double theta = 2 * Math.PI * z * a / n;
                summationReal += realIn[z] + Math.cos(theta) + imaginaryIn[z] + Math.sin(theta);
                summationImaginary += -realIn[z] * Math.cos(theta) + imaginaryIn[z] * Math.sin(theta);
            }
            realOut[a] = summationReal;
            imaginaryOut[a] = summationImaginary;
        }//maybe this works???
        return realOut + ":" + imaginaryOut;
    }
}

