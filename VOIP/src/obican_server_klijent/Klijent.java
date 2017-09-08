package obican_server_klijent; /**
 * Created by Stefan on 22.2.2017.
 */

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Random;
import javax.sound.sampled.*;

public class Klijent extends Application {

    private boolean stop_audio = false;
    private ByteArrayOutputStream outputStream;
    private TargetDataLine targetDataLine;
    private AudioInputStream input_play_stream;
    private SourceDataLine sourceLine;
    private int port2;
    private int port;

    public static void main(String args[]) {
        launch();
    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        final Button start = new Button("Start!");
        final Button stop = new Button("Stop");
        final Button play = new Button("Play");
        HBox hbox=new HBox();

        start.setDisable(false);
        stop.setDisable(true);
        play.setDisable(true);

        start.setOnMouseClicked(event -> {
            start.setDisable(true);
            stop.setDisable(false);
            play.setDisable(true);
            captureAudio();
            Thread t= new Thread(this::listen);
           t.start();

        });

        stop.setOnMouseClicked(event -> {
            start.setDisable(false);
            stop.setDisable(true);
            play.setDisable(false);
            stop_audio = true;
            targetDataLine.close();
        });

        play.setOnMouseClicked(event -> playAudio());

        primaryStage.setOnCloseRequest(event -> stop_audio=true);
        hbox.getChildren().addAll(start,play,stop);
        hbox.setAlignment(Pos.CENTER);
        primaryStage.setTitle("VOIP proba");
        primaryStage.setScene(new Scene(hbox,400,150));
        primaryStage.show();
    }

    private void listen() {
        try {
            Random rand=new Random();
            port2=rand.nextInt(5000)+1000;
            DatagramSocket serverSocket = new DatagramSocket(port+1); // +1 ?
            byte[] receiveData = new byte[512];
            while (!stop_audio) {
                DatagramPacket primljeni_paket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(primljeni_paket);
                System.out.println("Primljen paket : " + primljeni_paket.getAddress().getHostAddress() + " " + primljeni_paket.getPort());
                try {
                    byte audioData[] = primljeni_paket.getData();
                    InputStream inputStream = new ByteArrayInputStream(audioData);
                    AudioFormat audio_format = get_audio_format();
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


    private void captureAudio() {
        try {

            AudioFormat audio_format = get_audio_format();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audio_format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audio_format);
            targetDataLine.start();

            Zauzmi_kanal kanal=new Zauzmi_kanal();
            kanal.start();

        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }


    private void playAudio() {
        try {
            byte audioData[] = outputStream.toByteArray();
            InputStream byteInputStream = new ByteArrayInputStream(audioData);

            AudioFormat audio_format = get_audio_format();
            input_play_stream = new AudioInputStream(byteInputStream, audio_format, audioData.length / audio_format.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audio_format);
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceLine.open(audio_format);
            sourceLine.start();

            Thread playThread = new Thread(new PlayThread());
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private AudioFormat get_audio_format() {
        float sampleRate = 48000.0F;
        int sampleInbits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

    class Zauzmi_kanal extends Thread {

        byte tempBuffer[] = new byte[512];

        public void run() {

            outputStream = new ByteArrayOutputStream();

            stop_audio = false;
            try {
                Random rand=new Random();
                port = rand.nextInt(5000) + 1000;
                DatagramSocket clientSocket = new DatagramSocket(port);
                InetAddress ip_adresa = InetAddress.getByName("127.0.0.1");
                while (!stop_audio) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        DatagramPacket salji_paket = new DatagramPacket(tempBuffer, tempBuffer.length, ip_adresa, 1234);
                        outputStream.write(tempBuffer, 0, cnt);
                        clientSocket.send(salji_paket);
                    }
                }

                outputStream.close();
            } catch (Exception e) {
                System.out.println(e);

            }
        }


    }

    class PlayThread extends Thread {
        byte tempBuffer[] = new byte[10000];
        public void run() {
            try {
                int cnt;
                while ((cnt = input_play_stream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0)
                        sourceLine.write(tempBuffer, 0, cnt);

                }

            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}