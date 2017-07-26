/**
 * Created by Stefan on 22.2.2017.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class Server {

    private AudioInputStream input_play_stream;
    private SourceDataLine sourceLine;

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

    public static void main(String args[]) {
        new Server().runVOIP();
    }

    private void runVOIP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(1234);
            byte[] receiveData = new byte[10000];

            while (true) {
                DatagramPacket primljeni_paket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(primljeni_paket);
                byte audioData[] = primljeni_paket.getData();
                System.out.println("Primljen paket : " + primljeni_paket.getAddress().getHostAddress() + " " + primljeni_paket.getPort());
                try {

                    InputStream inputStream = new ByteArrayInputStream(audioData);
                    AudioFormat audio_format = getAudioFormat();
                    input_play_stream = new AudioInputStream(inputStream, audio_format, audioData.length / audio_format.getFrameSize());

                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audio_format);
                    sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceLine.open(audio_format);
                    sourceLine.start();

                    PlayThread play = new PlayThread();
                    play.start();
                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        public void run() {
            try {
                int len;
                while ((len = input_play_stream.read(tempBuffer, 0, tempBuffer.length)) != -1)
                    if (len > 0)
                        sourceLine.write(tempBuffer, 0,len);
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
