import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SIPSpeaker {

    private static List<session> sessions = new ArrayList<session>();

    public static void main(String[] args) {


        SIPSpeaker sipsp = new SIPSpeaker();
        conf_reader conf = new conf_reader(args);
        File f = new File(conf.getDefault_message());
        if (!f.exists()) {
            Voice voice;
            FreeTTS freetts;
            voice =
                    VoiceManager.getInstance().getVoice("kevin16");
            if (voice != null) {
                voice.allocate();
            }
            freetts = new FreeTTS(voice);
            String filename = conf.getDefault_message().substring(0, conf.getDefault_message().indexOf("."));
            SingleFileAudioPlayer sfap = new SingleFileAudioPlayer(filename, javax.sound.sampled.AudioFileFormat.Type.WAVE);
            voice.setAudioPlayer(sfap);
            voice.speak("This is the default message");
            sfap.close();
        }
        if (!conf.check()) {
            System.exit(-1);
        }
        if (!conf.parsing()) {
            System.exit(-1);
        }
        conf.conf_check_parameters();

        File fconf = new File(conf.getDefault_message());
        File fconfdefault = new File("default.cfg");
        File current =new File (conf.getMessage_wav());
        current.delete();
        if (!fconf.exists() && fconfdefault.exists()) {
            conf.setConfig_file_name("default.cfg");
            if (!conf.parsing()) {
                System.exit(-1);
            }
            conf.conf_check_parameters();
        }
        System.out.println(conf);

        ServerHttp serverhttp = new ServerHttp(conf);
        Thread httpserver = new Thread(serverhttp);
        httpserver.start();        
        byte[] receiveData = new byte[1024];

        try {
            DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
            DatagramSocket sock;
            sock = new DatagramSocket(conf.getSip_uri_port());



            String hostname = conf.getSip_uri_host();


            while (true) {
                sock.receive(dp);
                final InetAddress IPAddress = dp.getAddress();
                final int port = dp.getPort();
                final String sentence = new String(dp.getData());
                String ip = null;


                try {
                    Integer.parseInt(hostname.replace(".", ""));
                    ip = hostname;
                } catch (NumberFormatException e) {
                    try {
                        ip = InetAddress.getByName(hostname).getHostAddress().toString();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(SIPSpeaker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                sip_handler hand = new sip_handler(sessions, sentence, sock, ip, IPAddress, port, conf);
                Thread handler = new Thread(hand);
                handler.start();
            }

        } catch (BindException e) {
            System.out.println("Failed to bind the port for the sip socket");
            System.exit(-1);
        } catch (IOException e) {
            Logger.getLogger(SIPSpeaker.class.getName()).log(Level.SEVERE, null, e);
        }

    }
}