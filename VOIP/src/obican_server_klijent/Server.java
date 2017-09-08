package obican_server_klijent; /**
 * Created by Stefan on 22.2.2017.
 */
import java.net.*;
import javax.sound.sampled.*;

public class Server {

    private AudioInputStream input_play_stream;
    private SourceDataLine sourceLine;

    private AudioFormat getAudioFormat() {
        float sampleRate = 48000.0F;
        int sampleInbits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

    public static void main(String args[]) {
        new Server().runVOIP();
    }

    private void runVOIP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(1234);
            byte[] receiveData = new byte[512];

            DatagramPacket primljeni_paket = new DatagramPacket(receiveData, 500);

                try {

                    AudioFormat audio_format = getAudioFormat();

                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audio_format);
                    sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceLine.open(audio_format);
                    sourceLine.start();

                    FloatControl control = (FloatControl)sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
                    control.setValue(control.getMaximum());

                    while (true) {
                        serverSocket.receive( primljeni_paket ) ;
                        sourceLine.write(primljeni_paket.getData(), 0, 500);   //playing audio available in tempBuffer

                    }
                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(0);
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
