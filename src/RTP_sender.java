
import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.*;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;

public class RTP_sender {

    public void send(int mediaport, String fromip, InetAddress ip,conf_reader conf) {
        File f = new File(conf.getMessage_wav());
        if ((!f.exists() && conf.getMessage_text().equals(""))) {
            Voice voice;
            FreeTTS freetts;
            voice = VoiceManager.getInstance().getVoice("kevin16");
            if (voice != null) {
                voice.allocate();
            }
            freetts = new FreeTTS(voice);
            String filename=conf.getDefault_message().split(".wav")[0];
            SingleFileAudioPlayer sfap = new SingleFileAudioPlayer(filename, javax.sound.sampled.AudioFileFormat.Type.WAVE);
            voice.setAudioPlayer(sfap);
            voice.speak("Message by default");
            sfap.close();
            f= new File(conf.getDefault_message());
        }
        if (!conf.getMessage_text().equals("")){
             Voice voice;
            FreeTTS freetts;
            voice = VoiceManager.getInstance().getVoice("kevin16");
            if (voice != null) {
                voice.allocate();
            }
            freetts = new FreeTTS(voice);
            String filename=conf.getMessage_wav().split(".wav")[0];
            SingleFileAudioPlayer sfap = new SingleFileAudioPlayer(filename, javax.sound.sampled.AudioFileFormat.Type.WAVE);
            voice.setAudioPlayer(sfap);
            voice.speak(conf.getMessage_text());
            sfap.close();
        }

        Format format;
        format = new AudioFormat(AudioFormat.GSM_RTP, 8000, 8, 1);
        Processor processor = null;
        try {
            processor = Manager.createProcessor(f.toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoProcessorException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        processor.configure();

        while (processor.getState() != Processor.Configured) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        processor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));

        TrackControl track[] = processor.getTrackControls();

        boolean encodingOk = false;


        for (int i = 0; i < track.length; i++) {
            if (!encodingOk && track[i] instanceof FormatControl) {
                if (((FormatControl) track[i]).setFormat(format) == null) {

                    track[i].setEnabled(false);
                } else {
                    encodingOk = true;
                }
            } else {
                track[i].setEnabled(false);
            }
        }

        processor.realize();
        while (processor.getState() != processor.Realized) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if (encodingOk) {

            DataSource ds = null;

            try {

                RTPManager rtpmanager = RTPManager.newInstance();
                
                //Not working 
                //String hostname = conf.getSip_uri_host();
                //InetAddress inetaddress = InetAddress.getByName(hostname);
                //SessionAddress hostAddress = new SessionAddress(inetaddress,SessionAddress.ANY_PORT);
                
                //Only working for the loopback address
                SessionAddress hostAddress = new SessionAddress(InetAddress.getLocalHost(),SessionAddress.ANY_PORT);
                rtpmanager.initialize(hostAddress);
                SessionAddress TargetAddress = new SessionAddress(ip, mediaport);
                rtpmanager.addTarget(TargetAddress);

                ds = processor.getDataOutput();
                SendStream AudioSend = rtpmanager.createSendStream(ds, 0);
               
                AudioSend.start();
                Thread.sleep(1500); 
                 processor.start();
                try {
                    Thread.sleep((int) processor.getDuration().getSeconds() * 1000 + 3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RTP_sender.class.getName()).log(Level.SEVERE, null, ex);
                }
                AudioSend.close();
                rtpmanager.dispose();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }




        }


    }
}
