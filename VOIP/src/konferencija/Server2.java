package konferencija; /**
 * Created by SilentStorm1 on 12.5.2017..
 */
import java.net.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class Server2 {

    private AudioInputStream input_play_stream;
    private SourceDataLine sourceLine;

    private AudioFormat getAudioFormat() {
        float sampleRate = 48000.0F;
        int sampleInbits = 32;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

    public static void main(String args[]) {
        new Server2().runVOIP();
    }

    private void runVOIP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(1234);
            byte[] receiveData = new byte[8192];
            ArrayList<Integer> lista_portova=new ArrayList<>();
            DatagramPacket primljeni_paket = new DatagramPacket(receiveData, receiveData.length);
            while (true) {
                serverSocket.receive(primljeni_paket);
                byte audioData[] = primljeni_paket.getData();
                System.out.println("Primljen paket : " + primljeni_paket.getAddress().getHostAddress() + " " + primljeni_paket.getPort());
                if(!lista_portova.contains(primljeni_paket.getPort()))
                    lista_portova.add(primljeni_paket.getPort());

                for (Integer port:lista_portova) {
                    if(port!=primljeni_paket.getPort())
                    {
                        try {
                                DatagramSocket clientSocket = new DatagramSocket();
                                InetAddress ip_adresa = InetAddress.getByName("127.0.0.1");
                                DatagramPacket salji_paket = new DatagramPacket(audioData, primljeni_paket.getLength(), ip_adresa, port+1);
                                clientSocket.send(salji_paket);
                                clientSocket.close();
                            } catch (Exception e) {
                                e.printStackTrace();}
                        }
                }}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

