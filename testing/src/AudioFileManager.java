import javax.sound.sampled.*;
import java.io.*;

/*
    Class that handles audio I/O --reading, writing, etc

    when writing to the audio file, its a byte array. otherwise, its a short array

    when the audio file is transformed, this data changes as well
 */
public class AudioFileManager {

    private boolean dataSet = false;
    private ComplexDoubleFFT fft;
    private File audioFile  = null;
    private AudioInputStream audioIn = null;
    private DataInputStream dataIn  = null;
    private AudioFormat format;

    //both complex
    private short[] leftData;
    private short[] rightData;

    private static final int DEFAULTSAMPLERATE = 44100;
    private static final String WAVHEADER = "RIFF____WAVEfmt ____________________data";

    public AudioFileManager(String filepath){
        audioFile = new File(filepath);
        short[] data = byteArrToShortArr(readFile(audioFile));
        leftData = complexify(getLeftChannel(data));
        rightData = complexify(getRightChannel(data));
    }

    public AudioFileManager(File audioFileIn){
        audioFile = audioFileIn;
        short[] data = byteArrToShortArr(readFile(audioFile));
        leftData = complexify(getLeftChannel(data));
        rightData = complexify(getRightChannel(data));
    }

    public byte[] readFile(File file){
        byte[] data = null;
        try {
            byte[] read = new byte[8];
            FileInputStream in = new FileInputStream(file);
            in.read(read);
            int fileSize = 0;
            for(int i = 4; i < 8; i++){
                fileSize += read[i] * Math.pow(256, i - 4);
            }
            read = new byte[fileSize - 8];
            in.read(read);

            int dataStart = 40;
            for(int i = 0; i < read.length; i++){
                if(read[i] == 'd' && read[i+1] == 'a' &&read[i+2] == 't' &&read[i+3] == 'a'){
                    dataStart = i + 4;
                    break;
                }
            }
            data = new byte[fileSize - dataStart];
            for(int i = 0; i < read.length - dataStart; i++){
                data[i] = read[i + dataStart];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public short[] complexify(short[] realData){
        short[] ret = new short[realData.length * 2];
        for(int i = 0; i < realData.length; i++){
            ret[2*i] = realData[i];
            ret[2*i+1] = 0;
        }
        return ret;
    }

    public short[] getLeftChannel(){
        return leftData;
    }

    private short[] getLeftChannel(short[] totalData){
        short[] ret = new short[totalData.length/2];
        for(int i = 0; i < totalData.length; i+=2){
            ret[i / 2] = totalData[i];
        }
        return ret;
    }

    public short[] getRightChannel(){
        return rightData;
    }

    private short[] getRightChannel(short[] totalData){
        short[] ret = new short[totalData.length / 2];
        for(int i = 1; i < totalData.length; i+=2){
            ret[(i+1)/ 2 - 1] = totalData[i];
        }
        return ret;
    }

    public AudioFileManager(short[] samplesIn){
        rightData = new short[samplesIn.length / 2];
        leftData = new short[samplesIn.length / 2];
        for(int i = 0; i < samplesIn.length; i+=2){
            rightData[i / 2] = samplesIn[i];
            leftData[i / 2] = samplesIn[i+1];
        }
    }

    private byte[] shortArrToByteArr(short[] arr){
        byte[] ret = new byte[arr.length*2];
        for(int i = 0; i < arr.length; i++){
            ret[2 * i] = (byte) ((arr[i] / 256) & 255);
            ret[2 * i + 1] = (byte) (arr[i] & 255);
        }
        return ret;
    }

    private short[] byteArrToShortArr(byte[] arr){
        short[] ret = new short[arr.length / 2];
        for(int i = 0; i < arr.length; i+=2){
            ret[i / 2] = (short) (arr[i] + 256.0 * arr[i+1]);
        }
        return ret;
    }

    private short[] mergeData(short[] left, short[] right){
        if(left.length != right.length) return new short[]{};
        short[] ret = new short[left.length+right.length];
        for(int i = 0; i < ret.length; i+=2){
            ret[i] = left[i / 2];
            ret[i + 1] = right[i / 2];
        }
        return ret;
    }

    public String getName(){
        if (audioFile != null) {
            return audioFile.getName();
        } else {
            return "";
        }
    }

    //writes the file
    public void buildFile(String filepath) throws IOException {
        byte[] samples = shortArrToByteArr(mergeData(leftData, rightData));
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
        if(sampleRate == -1){
            sampleRate = DEFAULTSAMPLERATE;
        }
        byte[] header = new byte[40];
        byte[] headerSkeleton = WAVHEADER.getBytes();
        for(int i = 0; i < headerSkeleton.length; i++) {
            header[i] = headerSkeleton[i];
        }
        for(int i = 4; i < 8; i++) {
            header[i] = (byte) (chunkSize & 255);
            chunkSize /= 256;
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

    public Clip getClip(){
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(audioFile));
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clip;
    }

    //put transformations on the audio data below
    public void scale(double scale){
        for(int i = 0; i < leftData.length; i++){
            leftData[i] *= scale;
        }
        for(int i = 0; i < rightData.length; i++){
            rightData[i] *= scale;
        }
    }

    public void ftransform(){

        fft =  new ComplexDoubleFFT(leftData.length / 2);

        double[] toTransform = new double[leftData.length];
        for(int i = 0; i< toTransform.length; i+=1){
            toTransform[i] = (double) leftData[i];
        }
        fft.ft(toTransform);
        short[] res = new short[toTransform.length / 2];
        for(int i = 0; i < toTransform.length; i+=2){
            res[i / 2] = (short) toTransform[i];
        }
        leftData = res;

        fft =  new ComplexDoubleFFT(rightData.length / 2);

        toTransform = new double[rightData.length];
        for(int i = 0; i< toTransform.length; i+=1){
            toTransform[i] = (double) rightData[i];
        }
        fft.ft(toTransform);
        res = new short[toTransform.length / 2];
        for(int i = 0; i < toTransform.length; i+=2){
            res[i / 2] = (short) toTransform[i];
        }
        rightData = res;
    }

    public void btransform(){
        fft =  new ComplexDoubleFFT(leftData.length / 2);
        double[] toTransform = new double[leftData.length];
        for(int i = 0; i< toTransform.length; i+=1){
            toTransform[i] = (double) leftData[i];
        }
        fft.bt(toTransform);
        short[] res = new short[toTransform.length / 2];
        for(int i = 0; i < toTransform.length; i+=2){
            res[i / 2] = (short) toTransform[i];
        }
        leftData = res;

        fft =  new ComplexDoubleFFT(rightData.length / 2);
        toTransform = new double[rightData.length];
        for(int i = 0; i< toTransform.length; i+=1){
            toTransform[i] = (double) rightData[i];
        }
        fft.bt(toTransform);
        res = new short[toTransform.length / 2];
        for(int i = 0; i < toTransform.length; i+=2){
            res[i / 2] = (short) toTransform[i];
        }
        rightData = res;

    }

    public void pMult(short[] toMult){
        int end = toMult.length;
        if (leftData.length < end) end = leftData.length;
        for(int i = 0; i < end; i++){
            leftData[i] *= toMult[i];
            rightData[i] *= toMult[i];
        }
    }

    public void pAdd(short[] toAdd){
        int end = toAdd.length;
        if (rightData.length < end) end = rightData.length;
        for(int i = 0; i < end; i++){
            leftData[i] += toAdd[i];
            rightData[i] += toAdd[i];
        }

    }

}
