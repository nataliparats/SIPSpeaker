/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author antoine
 */
public class session {
    String callid;
    String from;
    String to;
    int mediaport;

    public int getMediaport() {
        return mediaport;
    }

    public void setMediaport(int mediaport) {
        this.mediaport = mediaport;
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        else {
            session s =(session)obj;
            return (s.callid.equals(this.callid) && s.from.equals(this.from) && s.to.equals(this.to));
        }
    }



    @Override
    public String toString() {
        return "session{" + "callid=" + callid + ", from=" + from + ", to=" + to + '}';
    }

    public String getCallid() {
        return callid;
    }

    public void setCallid(String callid) {
        this.callid = callid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

     public session(String callid, String from, String to) {
        this.callid = callid;
        this.from = from;
        this.to = to;
        this.mediaport=0;
    }   
    
    public session(String callid, String from, String to,int mediaport) {
        this.callid = callid;
        this.from = from;
        this.to = to;
        this.mediaport=mediaport;
    }
    
    
}
