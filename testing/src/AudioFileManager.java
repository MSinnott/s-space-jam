import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.util.Arrays;

/**
    Class that handles audio I/O --reading, writing, etc

    when writing to the audio file, its a byte array. otherwise, its a float array

    when the audio file is transformed, this data changes as well

    it handles data as channels - normally 2, but could have an arbitrarily large number
 */

public class AudioFileManager {

    private ComplexFloatFFT fft;
    private int fftlen;

    private File audioFile  = null;
    private String defaultName = "";

    private float[][] channels;

    private int sampleRate = DEFAULT_SAMPLE_RATE;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final String WAVHEADER = "RIFF____WAVEfmt ____________________data";

    private String filePath = null;

    /**
     * Creates audio file from a string
     * @param filepath path to file to use
     */
    public AudioFileManager(String filepath){
        audioFile = new File(filepath);
        float[] data = byteArrToFloatArr(readFile(audioFile));
        channels = complexify(split(data, 2));
        this.filePath = filepath;
    }

    /**
     * Creates audio file from a java File
     * @param audioFileIn file to use
     */
    public AudioFileManager(File audioFileIn) { //the one that I want -A
        audioFile = audioFileIn;
        filePath = audioFileIn.getAbsolutePath();

        float[] data = byteArrToFloatArr(readFile(audioFile));
        channels = complexify(split(data, 2));
    }

    /**
     * Creates a audio file from another AudioFileManager
     * @param fileManager AudioFileManager to clone (effectively)
     */
    public AudioFileManager(AudioFileManager fileManager){
        float[][] toCopy = fileManager.getChannels();
        channels = new float[toCopy.length][toCopy[0].length];
        System.arraycopy(toCopy, 0, channels, 0 , toCopy.length);
    }

    /**
     * Creates two-channel audio file from two arrays
     * @param leftSamples samples for left channel
     * @param rightSamples samples for right channel
     */
    public AudioFileManager(float[] leftSamples, float[] rightSamples){
        channels = new float[2][leftSamples.length];
        channels[0] = new float[leftSamples.length];
        channels[1] = new float[rightSamples.length];
        System.arraycopy(leftSamples, 0, channels[0], 0, leftSamples.length);
        System.arraycopy(rightSamples, 0, channels[1], 0, rightSamples.length);
        channels = complexify(channels);
    }

    /**
     * @return this objects channels
     */
    public float[][] getChannels(){
        return channels;
    }

    /**
     * Evenly splits a float[] into a set number of subArrays
     * @param toSplit array to split
     * @param numSplits number to splits to make
     * @return split array
     */
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

    /**
     * Adds imaginary components to the array
     * @param channelsIn input channels
     * @return complexified output
     */
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

    /**
     * Removes imaginary components from the array
     * @param channelsIn input channels
     * @return decomplexified output
     */
    public float[][] decomplexify(float[][] channelsIn){
        float[][] ret = new float[channelsIn.length][channelsIn[0].length/ 2];
        for (int i = 0; i < channelsIn.length; i++) {
            for (int j = 0; j < channelsIn[i].length - 1; j+=2) {
                ret[i][j / 2] = channelsIn[i][j];
            }
        }
        return ret;
    }

    /**
     * @param channelNum channel to get
     * @return the specified channel
     */
    public float[] getChannel(int channelNum){
        return channels[channelNum];
    }

    /**
     * Merges a channel group into a single channel
     * @param channelGroup group of channels to merge
     * @return new single channel
     */
    private float[] mergeData(float[][] channelGroup) {
        float[] ret = new float[channelGroup.length * channelGroup[0].length];
        for(int i = 0; i < channelGroup[0].length; i++){
            for (int j = 0; j < channelGroup.length; j++) {
                ret[i + j] = channelGroup[j][i];
            }
        }
        return ret;
    }

    /**
     * Converts a float[] to a byte[] -- can lose information!!
     * @param arr float[] to convert
     * @return converted byte[]
     */
    public byte[] floatArrToByteArr(float[] arr){
        byte[] ret = new byte[arr.length*2];
        for(int i = 0; i < arr.length; i++){
            ret[2 * i] = (byte) ((int) arr[i] & 255);
            ret[2 * i + 1] = (byte) (((int) arr[i] >> 8) & 255);
        }
        return ret;
    }

    /**
     * Converts a byte[] to a float[]
     * @param arr byte[] to convert
     * @return converted float[]
     */
    public float[] byteArrToFloatArr(byte[] arr){
        float[] ret = new float[arr.length / 2];
        for(int i = 0; i < ret.length; i+=1){
            ret[i] = (float) ((arr[2 * i] & 255) | arr[2*i+1] << 8);
        }
        return ret;
    }

    /**
     * @return this file filepath
     */
    public String getPath(){
        return filePath;
    }

    /**
     * @return this file's samplerate
     */
    public int getSampleRate(){
        return sampleRate;
    }

    /**
     * @return Name to be displayed on an AudioWindow -- with filesize
     */
    public String getName(){
        return (defaultName.equals("")) ? HumanReadable.memNumToReadable(channels[0].length) : defaultName;
    }

    public int getSoundLen(){
        return channels[0].length;
    }

    /**
     * @return time for song to be played -- in seconds
     */
    public float getSoundTime(){
        return channels[0].length / (2 * sampleRate);
    }

    /**
     * @return number of bytes the stored file will be
     */
    public long getNumBytes(){
        return channels[0].length * channels.length + 40;
    }

    /**
     * @param name new default name for this file
     */
    public void setDefaultName(String name){
        defaultName = name;
    }

    /**
     * Reads in a WAV file
     * @param file file to read
     * @return read in data
     */
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

            System.out.println(fileSize);

            int dataStart = 40;
            for(int i = 0; i < read.length; i++){
                if(read[i] == 'd' && read[i+1] == 'a' &&read[i+2] == 't' &&read[i+3] == 'a'){
                    dataStart = i+4;
                    break;
                }
            }
            data = new byte[fileSize - dataStart - 8]; // -8. b/cause it works
            System.arraycopy(read, dataStart, data, 0, read.length - dataStart);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * @return sound data in WAV format
     */
    public byte[] getSoundData(){
        return floatArrToByteArr(mergeData(decomplexify(channels)));
    }

    /**
     * Writes this file's data into a WAV file
     * @param filepath path to file
     * @throws IOException
     */
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

        System.arraycopy(header, 0, writeOut, 0, header.length);
        System.arraycopy(samples, 0, writeOut, 40, chunkSize - 40);

        fileOut.write(writeOut);
        fileOut.close();
        defaultName = audioFile.getName() + " - " + HumanReadable.memNumToReadable(chunkSize);
    }

    /**
     * Build a WAV header for this file
     * @param chunkSize size of data
     * @param sampleRate this file's SampleRate
     * @return built header
     */
    public byte[] buildHeader(int chunkSize, int sampleRate){
        if(sampleRate < 0){
            sampleRate = DEFAULT_SAMPLE_RATE;
        }
        byte[] header = new byte[40];
        byte[] headerSkeleton = WAVHEADER.getBytes();
        System.arraycopy(headerSkeleton, 0, header, 0, headerSkeleton.length);

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

    /**
     * @return this file's AudioFormat
     */
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

    public void hshift(int amt){
        if(amt > 0){
            for (int i = 0; i < channels.length; i++) {
                float[] shiftChannel = new float[channels[i].length + amt];
                System.arraycopy(channels[i], 0, shiftChannel, amt, channels[i].length);
                channels[i] = shiftChannel;
            }
        } else if(amt < 0){
            for (int i = 0; i < channels.length; i++) {
                float[] shiftChannel = new float[channels[i].length + amt];
                System.arraycopy(channels[i], -amt, shiftChannel, 0, shiftChannel.length);
                channels[i] = shiftChannel;
            }
        }
    }

    public void smallFFT(int fftSize){
        float[][][] toTransform = new float[channels.length]
                [channels.length / fftSize + ((channels.length % fftSize != 0) ? 1 : 0)][fftSize];
        fft = new ComplexFloatFFT(fftSize / 2);

        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < toTransform[0].length; j++) {
                System.arraycopy(channels[i], fftSize * j, toTransform[i][j], 0, fftSize);
            }
        }

        for(float[][] channel: toTransform){
            for(float[] smallData: channel){
                float[] checkDiff = new float[fftSize];
                System.arraycopy(smallData, 0, checkDiff, 0, fftSize);
                fft.ft(smallData);
                System.out.println(Arrays.toString(checkDiff));
                System.out.println(Arrays.toString(smallData));
            }
        }

        double normalizer = 1 / Math.sqrt(fftSize / 2);

        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < toTransform[0].length; j++) {
                for (int k = 0; k < fftSize; k++) {
                    channels[i][j * fftSize + k] = (float) (normalizer * toTransform[i][j][k]);
                }
            }
            System.out.println(Arrays.toString(channels[i]));
        }
    }

    public void ftransform(){
        int powOfTwo = 1;
        while (channels[0].length > powOfTwo) {
            powOfTwo *= 2;
        }

        if(fftlen != powOfTwo) {
            fft = new ComplexFloatFFT(powOfTwo / 2);
            fftlen = powOfTwo;
        }

        for (int i = 0; i < channels.length; i++) {
            float[] tempArr = new float[powOfTwo];
            System.arraycopy(channels[i], 0, tempArr, 0, channels[i].length);
            channels[i] = tempArr;
        }

        for (int i = 0; i < channels.length; i++) {

            fft.ft(channels[i]);

            double normalizer = 1 / Math.sqrt(channels[i].length / 2);

            for (int j = 0; j < channels[i].length; j += 1) {
                channels[i][j] *= normalizer;
            }
        }
    }

    public void btransform(){
        int powOfTwo = 1;
        while (channels[0].length > powOfTwo) {
            powOfTwo *= 2;
        }

        if(fftlen != powOfTwo) {
            fft = new ComplexFloatFFT(powOfTwo / 2);
            fftlen = powOfTwo;
        }

        for (int i = 0; i < channels.length; i++) {
            float[] tempArr = new float[powOfTwo];
            System.arraycopy(channels[i], 0, tempArr, 0, channels[i].length);
            channels[i] = tempArr;
        }

        for (int i = 0; i < channels.length; i++) {

            fft.bt(channels[i]);

            double normalizer = 1 / Math.sqrt(channels[i].length / 2);

            for (int j = 0; j < channels[i].length; j += 1) {
                channels[i][j] *= normalizer;
            }
        }
    }

    public void pMult(float[][] channelsIn){
        float[][] toTraverse = (channelsIn[0].length > channels[0].length) ? channels : channelsIn;
        float[][] resultant = (channelsIn[0].length < channels[0].length) ? channels : channelsIn;
        for (int i = 0; i < toTraverse.length; i++) {
            for (int j = 0; j < toTraverse[0].length; j += 2) {
                resultant[i][j] += toTraverse[i][j];
            }
        }
        channels = resultant;
    }

    public void pMult(AudioFileManager audioFileManager){
        pMult(audioFileManager.getChannels());
    }

    public void pAdd(float[][] channelsIn, int offest){
        int mostChannels = (channelsIn.length > channels.length) ? channelsIn.length : channels.length;
        int longestChannel = (channelsIn[0].length + offest > channels[0].length) ? channelsIn[0].length + offest : channels[0].length;
        float[][] resultant = new float[mostChannels][longestChannel];
        for (int i = 0; i < resultant.length; i++) {
            for (int j = 0; j < resultant[0].length; j += 2) {
                if(channels.length > i && channels[0].length > j) resultant[i][j] += channels[i][j];
                if(j >= offest && channelsIn.length > i && channelsIn[0].length + offest > j) resultant[i][j] += channelsIn[i][j - offest];
            }
        }
        channels = resultant;
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

    public void pAdd(AudioFileManager audioFileManager, boolean toEnd){
        pAdd(audioFileManager.getChannels(), (toEnd) ? channels[0].length : 0);
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

    public void addNoise(int noiseSteps, int noiseScale){
        ftransform();
        for (int i = 1; i < noiseSteps; i ++){
            for (int j = 0; j < channels.length; j++) {
                for (int k = 0; k < channels[0].length; k++) {
                    if(i*k < channels[0].length) channels[j][i*k] = channels[j][k] / (noiseScale);
                }
            }
        }
        btransform();
    }

    public void addHarmonics(float[] harmonicsToAdd){
        ftransform();
        for (int i = 0; i < channels.length; i++) {
            for (int j = channels[i].length - 1; j > 0; j--) {
                for (int k = 0; k < harmonicsToAdd.length; k++) {
                    if(j * k < channels[i].length) channels[i][j * k] += channels[i][j] * harmonicsToAdd[k];
                }
            }
        }
        btransform();
    }

    public void filter(float threshold, int stIndex, int endIndex, boolean remBelow){
        for (float[] channel: channels) {
            for (int i = stIndex; i < channel.length && i < endIndex; i++) {
                if(Math.abs(channel[i]) < threshold ==  remBelow) channel[i] = 0;
            }
        }
    }

    public void filter(float threshold, boolean remBelow){
        filter(threshold, 0, channels[0].length, remBelow);
    }

    public void makeAudible(){
        float avg = 0;
        int t = 1;
        for (float[] channel : channels){
                for (float x : channel) {
                    avg += (x - avg) / t;
                    ++t;
                }
        }
        float multiplier = 10 / avg;
        for (int i = 0; i < channels.length; i++) {
            for (int j = 0; j < channels[i].length; j++) {
                channels[i][j] *= multiplier;
            }
        }
    }

}