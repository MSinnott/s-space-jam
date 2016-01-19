import javax.sound.sampled.*;
import java.io.*;

/*
    Class that handles audio I/O --reading, writing, etc

    when writing to the audio file, its a byte array. otherwise, its a short array

    when the audio file is transformed, this data changes as well
 */
public class AudioFileManager {

    private ComplexDoubleFFT fft;
    private File audioFile  = null;
    private AudioInputStream audioIn = null;
    private DataInputStream dataIn  = null;
    private AudioFormat format;
    private byte[] samples = null;
    private static final int DEFAULTSAMPLERATE = 44100;
    private static final String WAVHEADER = "RIFF____WAVEfmt ____________________data";

    public AudioFileManager(String filepath){
        audioFile = new File(filepath);
        try {
            System.out.println(filepath);
            audioIn = AudioSystem.getAudioInputStream(audioFile);
            dataIn = new DataInputStream(audioIn);
            format = audioIn.getFormat();
            samples = new byte[(int)(audioIn.getFrameLength() * format.getFrameSize())];
            dataIn.readFully(samples);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AudioFileManager(File audioFileIn){
        audioFile = audioFileIn;
        try {

            audioIn = AudioSystem.getAudioInputStream(audioFile);
            dataIn = new DataInputStream(audioIn);
            format = audioIn.getFormat();
            samples = new byte[(int)(audioIn.getFrameLength() * format.getFrameSize())];
            dataIn.readFully(samples);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AudioFileManager(byte[] samplesIn){
        samples = samplesIn;
    }

    public AudioFileManager(short[] samplesIn){
        samples = getAudioBytes(samplesIn);
    }

    public short[] getAudioData(){
        short[] shortSamples = new short[samples.length / 2];
        for(int i = 0; i < shortSamples.length; i+=1) {
            shortSamples[i] = (short) (samples[2*i] + samples[2*i+1] * 256);
        }
        return shortSamples;
    }

    public static short[] getAudioData(byte[] bytes){
        short[] shortSamples = new short[bytes.length / 2];
        for(int i = 0; i < shortSamples.length; i+=1) {
            shortSamples[i] = (short) (bytes[2*i] + bytes[2*i+1] * 256);
        }
        return shortSamples;
    }

    public byte[] getAudioBytes(){
        return samples;
    }

    public static byte[] getAudioBytes(short[] audioData){
        byte[] audioBytes = new byte[2*audioData.length];
        for(int i = 0; i < audioData.length; i+=1){
            audioBytes[2*i+1] = (byte) (audioData[i] & 255);
            audioBytes[2*i] = (byte) ((audioData[i] / 256) & 255);
        }
        return audioBytes;
    }

    public void buildFile(String filepath) throws IOException {
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
    public short[] scale(double scale){
        short[] vals = getAudioData();
        for(int i = 0; i < vals.length; i++){
            vals[i] *= scale;
        }
        samples = getAudioBytes(vals);
        return vals;
    }

    public short[] ftransform(){
        short[] vals = getAudioData();
        System.out.println(4 * vals.length + 15);
        fft =  new ComplexDoubleFFT(vals.length);

        double[] toTransform = new double[vals.length * 2];
        for(int i = 0; i< toTransform.length; i+=2){
            toTransform[i] = (double) vals[i / 2];
            toTransform[i + 1] = 0;
        }
        fft.ft(toTransform);
        short[] res = new short[toTransform.length / 2];
        for(int i = 0; i < toTransform.length; i+=2){
            res[i / 2] = (short) toTransform[i];
        }
        samples = getAudioBytes(res);
        return res;
    }

    public short[] btransform(){
        short[] vals = getAudioData();
        fft =  new ComplexDoubleFFT(vals.length);
        double[] toTransform = new double[vals.length * 2];
        for(int i = 0; i< toTransform.length; i+=2){
            toTransform[i] = (double) vals[i / 2];
            toTransform[i + 1] = 0;
        }
        fft.bt(toTransform);
        short[] res = new short[toTransform.length / 2];
        for(int i = 0; i < toTransform.length; i+=2){
            res[i / 2] = (short) toTransform[i];
        }
        samples = getAudioBytes(res);
        return res;
    }

    public short[] pMult(short[] toMult){
        int end = toMult.length;
        short[] vals = getAudioData();
        if (vals.length < end) end = vals.length;
        for(int i = 0; i < end; i++){
            vals[i] *= toMult[i];
        }
        samples = getAudioBytes(vals);
        return vals;
    }

    public short[] pAdd(short[] toAdd){
        int end = toAdd.length;
        short[] vals = getAudioData();
        if (vals.length < end) end = vals.length;
        for(int i = 0; i < end; i++){
            vals[i] += toAdd[i];
        }
        samples = getAudioBytes(vals);
        return vals;
    }

    public short[] clip(int startVal, int endVal){
        for(int i = 0; i < samples.length; i++){
            if(i > startVal && i < endVal) { samples[i] = 0; }
        }
        return getAudioData(samples);
    }

}
