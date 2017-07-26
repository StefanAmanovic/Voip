/**
 * Created by SilentStorm1 on 12.5.2017..
 */
/**
 * Created by Stefan on 22.2.2017.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.*;

public class Server2 {

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
        new Server2().runVOIP();
    }

    private void runVOIP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(1234);
            byte[] receiveData = new byte[10000];
            ArrayList<Integer> lista_portova=new ArrayList<>();
            while (true) {
                DatagramPacket primljeni_paket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(primljeni_paket);
                byte audioData[] = primljeni_paket.getData();
                System.out.println("Primljen paket : " + primljeni_paket.getAddress().getHostAddress() + " " + primljeni_paket.getPort());
                if(!lista_portova.contains(primljeni_paket.getPort()))
                    lista_portova.add(primljeni_paket.getPort());

                for (Integer port:lista_portova) {
                    if(port!=primljeni_paket.getPort())
                    {
                        Thread t= new Thread(() -> {

                           while(audioData.length>0)
                            try {
                                Random rand=new Random();
                                 int port2=rand.nextInt(5000)+1000;

                                DatagramSocket clientSocket = new DatagramSocket();
                                InetAddress ip_adresa = InetAddress.getByName("127.0.0.1");
                                DatagramPacket salji_paket = new DatagramPacket(audioData, audioData.length, ip_adresa, port+1);

                                clientSocket.send(salji_paket);

                                clientSocket.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                        t.start();}
                }}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        public void run() {
            try {
                int cnt;
                while ((cnt = input_play_stream.read(tempBuffer, 0, tempBuffer.length)) != -1)
                    if (cnt > 0)
                        sourceLine.write(tempBuffer, 0, cnt);
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}

