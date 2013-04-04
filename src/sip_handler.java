
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class sip_handler implements Runnable {

    private List<session> sessions;
    private String sentence;
    private DatagramSocket sock;
    private String ip;
    private InetAddress IPAddress;
    private int port;
    private conf_reader conf;

    public sip_handler(List<session> sessions, String sentence, DatagramSocket sock, String ip, InetAddress IPAddress, int port, conf_reader conf) {
        this.sessions = sessions;
        this.sentence = sentence;
        this.sock = sock;
        this.ip = ip;
        this.IPAddress = IPAddress;
        this.port = port;
        this.conf = conf;
    }

    @Override
    public void run() {
        byte[] sendData = new byte[1024];
        UUID idOne = UUID.randomUUID();
        String tag = idOne.toString().replace("-", "");
        String from = sentence.substring(sentence.indexOf("From:") + 6, sentence.indexOf("\r", sentence.indexOf("From:") + 1));
        String callid = sentence.substring(sentence.indexOf("Call-ID:") + 9, sentence.indexOf("\r", sentence.indexOf("Call-ID:") + 1));
        String contact = sentence.substring(sentence.indexOf("To:") + 4, sentence.indexOf("\r", sentence.indexOf("To:") + 1));
        String to = contact;
        if (!to.contains(";tag=")) {
            to = to + ";tag=" + tag;
        }
        session test = new session(callid, from, to);
        Boolean started = sessions.contains(test);
        if (sentence.startsWith("INVITE")) {
            if (!started) {
                String cseq = sentence.substring(sentence.indexOf("CSeq:") + 6, sentence.indexOf("INVITE", sentence.indexOf("CSeq:") + 1) - 1);
                String via1 = sentence.substring(sentence.indexOf("Via:") + 5, sentence.indexOf("\r", sentence.indexOf("Via:") + 1));
                String o = sentence.substring(sentence.indexOf("o=") + 3, sentence.indexOf("IP4") + 3);
                String media = "a=direction:pasive\r\n" + "m=audio 49152 RTP/AVP 3 101\r\n" + "a=rtpmap:3 GSM/8000\r\n" + "a=rtpmap:101 telephone-event/8000\r\n" + "a=fmtp:1010-11,16\r\n";//sentence.substring(sentence.indexOf("a=")+3);session test= new session(callid,from,to,-1);
                String med = sentence.substring(sentence.indexOf("m=audio") + 8, sentence.indexOf(" ", sentence.indexOf("m=audio") + 8));

                String firstline = sentence.substring(sentence.indexOf("sip:") + 4, sentence.indexOf("\r\n"));
                String user = "";
                if (firstline.contains("@")) {
                    user = firstline.substring(0, firstline.indexOf("@"));
                }
                if (conf.getSip_uri_user().equals(user)) {
                    test.setMediaport(Integer.parseInt(med));
                    sessions.add(test);
                    try {
                        String ringing = "SIP/2.0 180 Ringing\r\n" + "Via: " + via1
                                + "\r\n" + "Content-Length: 0\r\n" + "Contact: " + contact + "\r\n"
                                + "Call-ID: " + callid + "\r\n" + "CSeq: " + cseq + " INVITE\r\n"
                                + "From: " + from + "\r\n" + "To:" + to + "\r\n\r\n";
                        sendData = ringing.getBytes();
                        DatagramPacket ring = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        sock.send(ring);
                        String sdp = "v=0\r\n" + "o=-" + o + " " + ip
                                + "\r\n" + "s=sipspeaker\r\n" + "c=IN IP4 " + ip + "\r\n" + "t=0 0\r\n" + media;
                        String ok = "SIP/2.0 200 OK\r\n" + "Via: " + via1 + "\r\n"
                                + "Content-Length:" + sdp.length() + "\r\n" + "Contact: " + contact
                                + "\r\n" + "Call-ID: " + callid + "\r\n" + "Content-Type:application/sdp\r\n"
                                + "CSeq: " + cseq + " INVITE\r\n" + "From: "
                                + from + "\r\n" + "To:" + to + "\r\n\r\n" + sdp;
                        sendData = ok.getBytes();
                        DatagramPacket OK = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        sock.send(OK);


                    } catch (Exception e) {
                        System.out.println("error invite");
                        sessions.remove(test);
                    }
                } else {
                    try {
                        String notfound = "SIP/2.0 404 Not Found\r\n" + "Via: " + via1
                                + "\r\n"
                                + "Call-ID: " + callid + "\r\n" + "CSeq: " + cseq + " INVITE\r\n"
                                + "From: " + from + "\r\n" + "To:" + to + "\r\n\r\n";
                        sendData = notfound.getBytes();
                        DatagramPacket NOTFOUND = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        sock.send(NOTFOUND);                     
                    } catch (IOException ex) {
                        Logger.getLogger(sip_handler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else {
                System.out.println("session already started");
            }
        } else if (sentence.startsWith("ACK")) {

            if (started) {
                int mediaport = sessions.get(sessions.indexOf(test)).getMediaport();
                String fromadd = "";
                if (from.indexOf("@") != -1) {
                    if (from.indexOf(":", from.indexOf("@") + 1) != -1) {
                        fromadd = from.substring(from.indexOf("@") + 1, from.indexOf(":", from.indexOf("@")));
                    } else {
                        fromadd = from.substring(from.indexOf("@") + 1, from.indexOf(">", from.indexOf("@")));
                    }
                } else {
                    if (from.indexOf(":", from.indexOf("<sip:") + 6) != -1) {
                        fromadd = from.substring(from.indexOf("<sip:") + 5, from.indexOf(":", from.indexOf("<sip:") + 6));
                    } else {
                        fromadd = from.substring(from.indexOf("<sip:") + 5, from.indexOf(">", from.indexOf("<sip:") + 6));
                    }

                }
                String fromip = "";
                try {
                    Integer.parseInt(fromadd.replace(".", ""));
                    fromip = fromadd;
                } catch (NumberFormatException e) {
                    try {
                        fromip = InetAddress.getByName(fromadd).getHostAddress().toString();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(sip_handler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                RTP_sender rtpsender = new RTP_sender();

                rtpsender.send(mediaport, IPAddress.getHostAddress().toString(),IPAddress,conf);
                String cseq = sentence.substring(sentence.indexOf("CSeq:") + 6, sentence.indexOf("ACK", sentence.indexOf("CSeq:") + 1) - 1);
                String via1 = sentence.substring(sentence.indexOf("Via:") + 5, sentence.indexOf("\r", sentence.indexOf("Via:") + 1));
                String bye = "BYE sip:" + IPAddress.getHostAddress().toString()
                        + " SIP/2.0\r\n" + "Via: " + via1 + "\r\n"
                        + "Content-Length:0\r\n" + "Call-ID: " + callid + "\r\n" + "CSeq: " + cseq
                        + " BYE\r\n" + "User-Agent: sipspeaker\r\n" + "Max-Forwards: 70\r\n"
                        + "From: " + to + ";tag=" + tag + "\r\n" + "To:" + from
                        + "\r\n\r\n";
                sendData = bye.getBytes();
                DatagramPacket BYE = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                try {
                    sock.send(BYE);
                } catch (IOException ex) {
                    Logger.getLogger(sip_handler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }




        } else if (sentence.startsWith("BYE")) {
            String cseq = sentence.substring(sentence.indexOf("CSeq:") + 6, sentence.indexOf("BYE", sentence.indexOf("CSeq:") + 1) - 1);
            String via1 = sentence.substring(sentence.indexOf("Via:") + 5, sentence.indexOf("\r", sentence.indexOf("Via:") + 1));

            String okbyebye = "SIP/2.0 200 OK\r\n"
                    + "Via: " + via1 + "\r\n"
                    + "Call-ID: " + callid + "\r\n"
                    + "CSeq: " + cseq + " BYE\r\n"
                    + "From: " + from + "\r\n"
                    + "To:" + to + "\r\n\r\n";
            sendData = okbyebye.getBytes();
            DatagramPacket OKBYE = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            try {
                sock.send(OKBYE);
            } catch (Exception e) {
                System.out.println("Error sending the last ok");
            }
            if (started) {
                sessions.remove(test);
            }
        } else if (sentence.startsWith("CANCEL")) {
        }
    }
}
