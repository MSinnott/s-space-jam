import java.io.*;

/*
    Class that handles audio I/O --reading, writing, etc

    when writing to the audio file, its a byte array. otherwise, its a float array

    when the audio file is transformed, this data changes as well

    it handles data as two channels  - left & right
 */

public class AudioFileManager {

    private ComplexDoubleFFT fft;
    private File audioFile  = null;
    private String defaultName = "";

    //both complex
    private float[] leftData;
    private float[] rightData;

    private static final int DEFAULTSAMPLERATE = 44100;
    private static final String WAVHEADER = "RIFF____WAVEfmt ____________________data";

    private String filePath = null;

    public AudioFileManager(String filepath){
        audioFile = new File(filepath);
        float[] data = byteArrToFloatArr(readFile(audioFile));
        leftData = complexify(getLeftChannel(data));
        rightData = complexify(getRightChannel(data));
        this.filePath = filepath;
    }

    public AudioFileManager(File audioFileIn){
        audioFile = audioFileIn;
        float[] data = byteArrToFloatArr(readFile(audioFile));
        leftData = complexify(getLeftChannel(data));
        rightData = complexify(getRightChannel(data));
        filePath = audioFileIn.getAbsolutePath();
    }

    public AudioFileManager(float[] samplesIn){
        rightData = new float[samplesIn.length / 2];
        leftData = new float[samplesIn.length / 2];
        for(int i = 0; i < samplesIn.length; i+=2){
            rightData[i / 2] = samplesIn[i];
            leftData[i / 2] = samplesIn[i+1];
        }
        rightData = complexify(rightData);
        leftData = complexify(leftData);
    }

    public AudioFileManager(AudioFileManager fileManager){
        float[] leftIn = fileManager.getLeftChannel();
        float[] rightIn = fileManager.getRightChannel();
        leftData = new float[leftIn.length];
        rightData = new float[rightIn.length];
        for(int i = 0; i < leftIn.length; i++){
            leftData[i] = leftIn[i];
            rightData[i] = rightIn[i];
        }
    }

    //adds imaginary components
    public float[] complexify(float[] realData){
        float[] ret = new float[realData.length * 2];
        for(int i = 0; i < realData.length; i++){
            ret[2*i] = realData[i];
            ret[2*i+1] = 0;
        }
        return ret;
    }

    //removes imaginary components
    public float[] decomplexify(float[] complexData){
        float[] ret = new float[complexData.length / 2];
        for(int i = 0; i < ret.length; i++){
            ret[i] = complexData[2 * i];
        }
        return ret;
    }

    //returns the left channel
    public float[] getLeftChannel(){
        return leftData;
    }

    //builds the left channel
    private float[] getLeftChannel(float[] totalData){
        float[] ret = new float[totalData.length/2];
        for(int i = 0; i < totalData.length; i+=2){
            ret[i / 2] = totalData[i];
        }
        return ret;
    }

    //returns the right channel
    public float[] getRightChannel(){
        return rightData;
    }

    //builds the right channel
    private float[] getRightChannel(float[] totalData){
        float[] ret = new float[totalData.length / 2];
        for(int i = 1; i < totalData.length; i+=2){
            ret[(i+1)/ 2 - 1] = totalData[i];
        }
        return ret;
    }

    //merges the left + right channels
    private float[] mergeData(float[] left, float[] right) throws Exception {
        if(left.length != right.length) throw new Exception("UnmatchedArrayException");
        float[] ret = new float[left.length+right.length];
        for(int i = 0; i < ret.length; i+=2){
            ret[i] = left[i / 2];
            ret[i + 1] = right[i / 2];
        }
        return ret;
    }

    //gets total audio data
    public float[] getMergedData(){
        try {
            return mergeData(leftData, rightData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new float[]{};
    }

    //array converter
    private byte[] floatArrToByteArr(float[] arr){
        byte[] ret = new byte[arr.length*2];
        for(int i = 0; i < arr.length; i++){
            ret[2 * i] = (byte) ((int) arr[i] & 255);
            ret[2 * i + 1] = (byte) (((int) arr[i] >> 8) & 255);
        }
        return ret;
    }

    //array converter
    private float[] byteArrToFloatArr(byte[] arr){
        float[] ret = new float[arr.length / 2];
        for(int i = 0; i < ret.length; i+=1){
            ret[i] = (float) ((arr[2 * i] & 255) | arr[2*i+1] << 8);
        }
        return ret;
    }

    public String getPath(){
        return filePath;
    }

    //returns file name
    public String getName(){
        if (audioFile != null) {
            return audioFile.getName();
        } else {
            return defaultName;
        }
    }

    public void setDefaultName(String name){
        defaultName = name;
    }

    //reads in the file
    public byte[] readFile(File file){
        byte[] data = null;
        try {
            byte[] read = new byte[8];
            FileInputStream in = new FileInputStream(file);
            in.read(read);
            int fileSize = 0;
            for(int i = 7; i >= 4; i--){
                fileSize = (fileSize << 8) | (read[i] & 255);
            }
            read = new byte[fileSize - 8];
            in.read(read);

            int dataStart = 40;
            for(int i = 0; i < read.length; i++){
                if(read[i] == 'd' && read[i+1] == 'a' &&read[i+2] == 't' &&read[i+3] == 'a'){
                    dataStart = i+4;
                    break;
                }
            }
            data = new byte[fileSize - dataStart - 8]; // -8. b/cause it works
            for(int i = 0; i < read.length - dataStart; i++){
                data[i] = read[i + dataStart];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    //writes the file
    public void buildFile(String filepath) throws IOException {
        if(!filepath.contains(".wav")) filepath += ".wav";
        this.filePath = filepath;
        byte[] samples = new byte[0];
        try {
            samples = floatArrToByteArr(mergeData(decomplexify(leftData), decomplexify(rightData)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int chunkSize = samples.length + 40;
        audioFile = new File(filepath);
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(audioFile));
        byte[] writeOut = new byte[chunkSize];
        byte[] header = buildHeader(chunkSize, DEFAULTSAMPLERATE);
        for(int i = 0; i < header.length; i++){
            writeOut[i] = header[i];
        }
        for(int i = 40; i < chunkSize; i++){
            writeOut[i] = samples[i - 40];
        }
        fileOut.write(writeOut);
        fileOut.close();
    }

    //builds a standard WAV header for the file
    public static byte[] buildHeader(int chunkSize, int sampleRate){
        if(sampleRate < 0){
            sampleRate = DEFAULTSAMPLERATE;
        }
        byte[] header = new byte[40];
        byte[] headerSkeleton = WAVHEADER.getBytes();
        for(int i = 0; i < headerSkeleton.length; i++) {
            header[i] = headerSkeleton[i];
        }
        for(int i = 4; i < 8; i++) {
            header[i] = (byte) (chunkSize & 255);
            chunkSize >>= 8;
        }
        header[16] = (byte) (16);
        header[17] = (byte) (0);
        header[18] = (byte) (0);
        header[19] = (byte) (0);
        header[20] = (byte) (1);
        header[21] = (byte) (0);
        header[22] = (byte) (2);
        header[23] = (byte) (0);
        int writeVal = sampleRate;
        for(int i = 24; i < 28; i++) {
            header[i] = (byte) (writeVal & 255);
            writeVal /= 256;
        }
        int byteRate = 4 * sampleRate;
        for(int i = 28; i < 32; i++){
            header[i] = (byte) (byteRate & 255);
            byteRate /= 256;
        }
        header[32] = (byte) (4);
        header[33] = (byte) (0);
        header[34] = (byte) (16);
        header[35] = (byte) (0);

        return header;
    }

    //put transformations on the audio data below \/

    public void scale(double scale){
        for(int i = 0; i < leftData.length; i++){
            leftData[i] *= scale;
            rightData[i] *= scale;
        }
    }

    public void ftransform(){
        double powOfTwo = 1;
        while (leftData.length / powOfTwo > 1) {
            powOfTwo*=2;
        }

        double[] toTransformLeft = new double[(int) powOfTwo];
        double[] toTransformRight = new double[(int) powOfTwo];

        for(int i = 0; i < powOfTwo; i ++){
            if(i < leftData.length) toTransformLeft[i] = leftData[i]; else toTransformLeft[i] = 0;
            if(i < rightData.length) toTransformRight[i] = rightData[i]; else toTransformRight[i] = 0;
        }

        fft =  new ComplexDoubleFFT(toTransformRight.length / 2);
        fft.ft(toTransformLeft);
        fft.ft(toTransformRight);

        float[] resLeft = new float[toTransformLeft.length];
        float[] resRight = new float[toTransformLeft.length];

        double normalizer = 1 / Math.sqrt(toTransformLeft.length / 2);

        for(int i = 0; i < toTransformLeft.length; i+=1){
            resLeft[i] = (float) (toTransformLeft[i] * normalizer);
            resRight[i] = (float) (toTransformRight[i] * normalizer);
        }

        leftData = resLeft;
        rightData = resRight;

    }

    public void btransform(){

        double powOfTwo = 1;
        while (leftData.length / powOfTwo > 1) {
            powOfTwo*=2;
        }

        double[] toTransformLeft = new double[(int) powOfTwo];
        double[] toTransformRight = new double[(int) powOfTwo];

        for(int i = 0; i < powOfTwo; i ++){
            if(i < leftData.length) toTransformLeft[i] = leftData[i]; else toTransformLeft[i] = 0;
            if(i < rightData.length) toTransformRight[i] = rightData[i]; else toTransformRight[i] = 0;
        }

        fft =  new ComplexDoubleFFT(toTransformRight.length / 2);
        fft.bt(toTransformLeft);
        fft.bt(toTransformRight);

        float[] resLeft = new float[toTransformLeft.length];
        float[] resRight = new float[toTransformLeft.length];

        double normalizer = 1 / Math.sqrt(toTransformLeft.length / 2);

        for(int i = 0; i < toTransformLeft.length; i+=1){
            resLeft[i] = (float) (toTransformLeft[i] * normalizer);
            resRight[i] = (float) (toTransformRight[i] * normalizer);
        }

        leftData = resLeft;
        rightData = resRight;

    }

    public void pMult(float[] toMult){
        int end = toMult.length;
        if (leftData.length < end) end = leftData.length;
        for(int i = 0; i < end; i++){
            leftData[i] *= toMult[i];
            rightData[i] *= toMult[i];
        }
    }

    public void pAdd(float[] toAdd){
        int end = toAdd.length;
        if (rightData.length < end) end = rightData.length;
        for(int i = 0; i < end; i++){
            leftData[i] += toAdd[i];
            rightData[i] += toAdd[i];
        }

    }

}
