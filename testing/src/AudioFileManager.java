import javax.sound.sampled.AudioFormat;
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

    private float[][] channels;

    private int sampleRate = DEFAULT_SAMPLE_RATE;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final String WAVHEADER = "RIFF____WAVEfmt ____________________data";

    private String filePath = null;
    //5 constructors that override each other

    public AudioFileManager(String filepath){
        audioFile = new File(filepath);
        float[] data = byteArrToFloatArr(readFile(audioFile));
        channels = complexify(split(data, 2));
        this.filePath = filepath;
    }

    public AudioFileManager(File audioFileIn) { //the one that I want -A
        audioFile = audioFileIn;
        filePath = audioFileIn.getAbsolutePath();

        float[] data = byteArrToFloatArr(readFile(audioFile));
        channels = complexify(split(data, 2));
    }

    public AudioFileManager(AudioFileManager fileManager){
        float[][] toCopy = fileManager.getChannels();
        System.arraycopy(toCopy, 0, channels, 0 , toCopy.length);
    }

    public AudioFileManager(float[] leftSamples, float[] rightSamples){
        channels = new float[2][leftSamples.length];
        channels[0] = new float[leftSamples.length];
        channels[1] = new float[rightSamples.length];
        System.arraycopy(leftSamples, 0, channels[0], 0, leftSamples.length);
        System.arraycopy(rightSamples, 0, channels[1], 0, rightSamples.length);
        channels = complexify(channels);
    }

    public float[][] getChannels(){
        return channels;
    }

    public float[][] split(float[] toSplit, int numSplits){
        float[][] ret = new float[numSplits][toSplit.length / numSplits + ((toSplit.length % numSplits == 0) ? 0 : 1)];
        int loc = 0;
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[i].length; j++, loc++) {
                ret[i][j] = toSplit[loc];
            }
        }
        return ret;
    }

    //adds imaginary components
    public float[][] complexify(float[][] channelsIn){
        float[][] ret = new float[channelsIn.length][channelsIn[0].length * 2];
        for (int i = 0; i < channelsIn.length; i++) {
            for (int j = 0; j < channelsIn[i].length; j++) {
                ret[i][2 * j] = channelsIn[i][j];
                ret[i][2 * j + 1] = 0;
            }
        }
        return ret;
    }

    //removes imaginary components
    public float[][] decomplexify(float[][] channelsIn){
        float[][] ret = new float[channelsIn.length][channelsIn[0].length/ 2];
        for (int i = 0; i < channelsIn.length; i++) {
            for (int j = 0; j < channelsIn[i].length; j+=2) {
                ret[i][j / 2] = channelsIn[i][j];
            }
        }
        return ret;
    }

    public float[] getChannel(int channelNum){
        return channels[channelNum];
    }

    //merges channels
    private float[] mergeData(float[][] channelGroup) {
        float[] ret = new float[channelGroup.length * channelGroup[0].length];
        for(int i = 0; i < channelGroup[0].length; i++){
            for (int j = 0; j < channelGroup.length; j++) {
                ret[i + j] = channelGroup[j][i];
            }
        }
        return ret;
    }

    //array converter
    public byte[] floatArrToByteArr(float[] arr){
        byte[] ret = new byte[arr.length*2];
        for(int i = 0; i < arr.length; i++){
            ret[2 * i] = (byte) ((int) arr[i] & 255);
            ret[2 * i + 1] = (byte) (((int) arr[i] >> 8) & 255);
        }
        return ret;
    }

    //array converter
    public float[] byteArrToFloatArr(byte[] arr){
        float[] ret = new float[arr.length / 2];
        for(int i = 0; i < ret.length; i+=1){
            ret[i] = (float) ((arr[2 * i] & 255) | arr[2*i+1] << 8);
        }
        return ret;
    }

    public String getPath(){
        return filePath;
    }

    public int getSampleRate(){
        return sampleRate;
    }

    //returns file name
    public String getName(){
        return (defaultName.equals("")) ? HumanReadable.memNumToReadable(channels[0].length) : defaultName;
    }

    public float getSoundTime(){
        return channels[0].length / (2 * sampleRate);
    }

    public long getNumBytes(){
        return channels[0].length * channels.length;
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

    public byte[] getSoundData(){
        return floatArrToByteArr(mergeData(decomplexify(channels)));
    }

    //writes the file
    public void buildFile(String filepath) throws IOException {
        if(!filepath.contains(".wav") && !filepath.contains("mp3") ) filepath += ".wav";
        this.filePath = filepath;
        byte[] samples = new byte[0];
        try {
            samples = getSoundData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int chunkSize = samples.length / 2 + 40;
        audioFile = new File(filepath);
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(audioFile));
        byte[] writeOut = new byte[chunkSize];
        byte[] header = buildHeader(chunkSize, DEFAULT_SAMPLE_RATE);
        for(int i = 0; i < header.length; i++){
            writeOut[i] = header[i];
        }
        for(int i = 40; i < chunkSize; i++){
            writeOut[i] = samples[i - 40];
        }
        fileOut.write(writeOut);
        fileOut.close();
        defaultName = audioFile.getName() + " - " + HumanReadable.memNumToReadable(chunkSize);
    }

    //builds a standard WAV header for the file
    public byte[] buildHeader(int chunkSize, int sampleRate){
        if(sampleRate < 0){
            sampleRate = DEFAULT_SAMPLE_RATE;
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
        int numChannels = channels.length;
        for(int i = 22; i < 24; i++) {
            header[i] = (byte) (numChannels & 255);
            numChannels /= 256;
        }
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

    public AudioFormat getAudioFormat(){
        return new AudioFormat(sampleRate, 16, channels.length, true, false);
    }

    //put transformations on the audio data below \/
    public void vshift(float amt, int stIndex, int endIndex){
        for(float[] toShift: channels) {
            for (int i = stIndex; i < toShift.length && i < endIndex; i++) {
                toShift[i] += amt;
            }
        }
    }

    public void vshift(float amt){
        vshift(amt, 0, channels[0].length);
    }

    public void vscale(float scale, int stIndex, int endIndex){
        for(float[] toShift: channels) {
            for (int i = stIndex; i < toShift.length && i < endIndex; i++) {
                toShift[i] *= scale;
            }
        }
    }

    public void vscale(float amt){
        vscale(amt, 0, channels[0].length);
    }

    public void smallFFT(int fftSize){
        double[][][] toTransform = new double[channels.length]
                [channels.length / fftSize + ((channels.length % fftSize != 0) ? 1 : 0)][fftSize];
        fft = new ComplexDoubleFFT(fftSize / 2);

        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < toTransform[0].length; j++) {
                for (int k = 0; k < fftSize; k++) {
                    toTransform[i][j][k] = channels[i][k + fftSize * j];
                }
            }
        }

        for(double[][] channel: toTransform){
            for(double[] smallData: channel){
                fft.ft(smallData);
            }
        }

        double normalizer = 1 / Math.sqrt(fftSize / 2);

        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < toTransform[0].length; j++) {
                for (int k = 0; k < fftSize; k++) {
                    channels[i][j * fftSize + k] = (float) (normalizer * toTransform[i][j][k]);
                }
            }
        }
    }

    public void ftransform(){
        double powOfTwo = 1;
        while (channels[0].length / powOfTwo > 1) {
            powOfTwo *= 2;
        }

        double[] toTransform = new double[(int) powOfTwo];
        fft = new ComplexDoubleFFT(toTransform.length / 2);

        for (int i = 0; i < channels.length; i++) {
            float[] toShift = channels[i];

            for (int j = 0; j < powOfTwo; j++) {
                if (j < toShift.length) toTransform[j] = toShift[j];
                else toTransform[j] = 0;

            }

            fft.ft(toTransform);

            float[] res = new float[toTransform.length];

            double normalizer = 1 / Math.sqrt(toTransform.length / 2);

            for (int j = 0; j < toTransform.length; j += 1) {
                res[j] = (float) (toTransform[j] * normalizer);
            }

            channels[i] = res;
        }
    }

    public void btransform(){
        double powOfTwo = 1;
        while (channels[0].length / powOfTwo > 1) {
            powOfTwo *= 2;
        }

        double[] toTransform = new double[(int) powOfTwo];
        fft = new ComplexDoubleFFT(toTransform.length / 2);

        for (int i = 0; i < channels.length; i++) {
            float[] toShift = channels[i];

            for (int j = 0; j < powOfTwo; j++) {
                if (j < toShift.length) toTransform[j] = toShift[j];
                else toTransform[j] = 0;

            }

            fft.bt(toTransform);

            float[] res = new float[toTransform.length];

            double normalizer = 1 / Math.sqrt(toTransform.length / 2);

            for (int j = 0; j < toTransform.length; j += 1) {
                res[j] = (float) (toTransform[j] * normalizer);
            }

            channels[i] = res;
        }
    }

    public void pMult(float[][] channels0){
        int numArr = (channels0.length > channels.length) ? channels.length : channels0.length;

        for (int i = 0; i < numArr; i++) {
            int arrInd = (channels0[i].length > channels[i].length) ? channels[i].length : channels0[i].length;
            for (int j = 0; j < arrInd; j++) {
                channels[i][j] *= channels0[i][j];
            }
        }
    }

    public void pMult(AudioFileManager audioFileManager){
        pMult(audioFileManager.getChannels());
    }

    public void pAdd(float[][] channelsIn, int offset){
        int numArr = (channelsIn.length < channels.length) ? channels.length : channelsIn.length;
        int arrInd = (channelsIn[0].length + offset < channels[0].length) ? channels[0].length : channelsIn[0].length + offset;
        float[][] result = new float[numArr][arrInd];
        for (int i = 0; i < numArr; i++) {
            for (int j = 0; j < arrInd; j++) {
                if(channels.length > i && channels[i].length > j) {
                    result[i][j] += channels[i][j];
                }
                if(j >= offset && channelsIn.length > i && channelsIn[i].length > j - offset) {
                    result[i][j] += channelsIn[i][j - offset];
                }
            }
        }
        channels = result;
    }

    public void boxcarFilter(int boxWidth){
        boxcarFilter(boxWidth, 0, channels[0].length);
    }

    public void boxcarFilter(int boxWidth, int startIndex, int endIndex){
        float[][] filtered = new float[channels.length][channels[0].length];
        float box = 0;
        for (int i = 0; i < channels.length; i++) {
            for (int j = startIndex; j < channels[0].length && j < endIndex; j++) {
                box += channels[i][j];
                if(j > boxWidth) box -= channels[i][j - boxWidth];
                if(j > boxWidth / 2) filtered[i][j - boxWidth / 2] = box / boxWidth;
            }
        }
        channels = filtered;
    }

    public void zeroFrom(int start, int end){
        if(start < 0) start = 0;
        if(end > channels[0].length) end = channels[0].length;
        if(start > end) return;
        for(float[] channel: channels) {
            for (int i = start; i < end && i < channel.length; i++) {
                channel[i] = 0;
            }
        }
    }

    public void pAdd(AudioFileManager audioFileManager, int offset){
        pAdd(audioFileManager.getChannels(), offset);
    }

    public void trim(){
        int stZeroF = 0, endZeroF = channels[0].length;
        for (float[] channel : channels){
            int i = stZeroF;
            while (channel[i] == 0){ i++; }
            if(endZeroF > i) endZeroF = i;
        }
        int stZeroE = 0, endZeroE = channels[0].length;
        for (float[] channel : channels){
            int i = endZeroE - 1;
            while (channel[i] == 0){ i--; }
            if(stZeroE < i) stZeroE = i;
        }
        float[][] newArrs = new float[channels.length][stZeroE - endZeroF];
        for (int i = 0; i < newArrs.length; i++) {
            System.arraycopy(channels[i], endZeroF, newArrs[i], 0, stZeroE - endZeroF);
        }
        channels = newArrs;
    }

}