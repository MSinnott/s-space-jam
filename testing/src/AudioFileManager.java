import javax.sound.sampled.*;
import java.io.*;

/*
    Class that handles audio I/O --reading, writing, etc
 */
public class AudioFileManager {

    private File audioFile  = null;
    private AudioInputStream audioIn = null;
    private DataInputStream dataIn  = null;
    private AudioFormat format;
    private byte[] samples = null;
    private int sampleRate = 22050;
    private int byteRate = 88200;

    private static String WAVHEADER = "RIFF____WAVEfmt ____________________data";

    public AudioFileManager(String filepath){
        audioFile = new File(filepath);
        try {

            audioIn = AudioSystem.getAudioInputStream(audioFile);
            dataIn = new DataInputStream(audioIn);
            format = audioIn.getFormat();
            samples = new byte[(int)(audioIn.getFrameLength() * format.getFrameSize())];
            System.out.println(samples.length);
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
            System.out.println(samples.length);
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

    public AudioFileManager(double[] samplesIn){
        samples = getAudioBytes(samplesIn);
    }

    public double[] getAudioData(){
        double[] doubleSamples = new double[samples.length];
        for(int i = 0; i < samples.length; i++) {
            doubleSamples[i] = (double) samples[i];
        }
        return doubleSamples;
    }

    public static double[] getAudioData(byte[] bytes){
        double[] doubleSamples = new double[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            doubleSamples[i] = (double) bytes[i];
        }
        return doubleSamples;
    }

    public byte[] getAudioBytes(){
        return samples;
    }

    public static byte[] getAudioBytes(double[] audioData){
        byte[] audioBytes = new byte[audioData.length];
        for(int i = 0; i < audioData.length; i++){
            audioBytes[i] = (byte) audioData[i];
        }
        return audioBytes;
    }

    //!!!!!!!!!!!!!!!!!!!needs testing!!!!!!!!!!!!!!!!!!!
    public void buildFile(String filepath) throws IOException {
        int chunkSize = samples.length + 40;
        audioFile = new File(filepath);
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(audioFile));
        byte[] writeOut = buildHeader(chunkSize);
        for(int i = 40; i < chunkSize; i++){
            writeOut[i] = samples[i];
        }
        fileOut.write(writeOut);
    }

    public byte[] buildHeader(int chunkSize){
        byte[] header = new byte[chunkSize];
        byte[] headerSkeleton = WAVHEADER.getBytes();
        for(int i = 0; i < headerSkeleton.length; i++) {
            header[i] = headerSkeleton[i];
        }
        for(int i = 4; i < 8; i++) {
            header[i] = (byte) (chunkSize % 16);
            chunkSize /= 100;
        }
        header[20] = (byte) (16);
        header[21] = (byte) (0);
        header[22] = (byte) (0);
        header[23] = (byte) (0);
        header[24] = (byte) (1);
        header[25] = (byte) (0);
        header[26] = (byte) (2);
        header[27] = (byte) (0);
        for(int i = 28; i < 32; i++) {
            header[i] = (byte) (sampleRate % 16);
            sampleRate /= 100;
        }
        byteRate = 4 * sampleRate;
        for(int i = 32; i < 36; i++){
            header[i] = (byte) (4 * byteRate % 16);
            byteRate /= 100;
        }
        header[36] = (byte) (4);
        header[37] = (byte) (0);
        header[38] = (byte) (1);
        header[39] = (byte) (0);

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

}
