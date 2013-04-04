
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public class conf_reader {

    private String[] args;
    String command_line = "";
    private String config_file_name = "default.cfg";
    private String default_message = "default.wav";
    private String message_wav = "current.wav";
    private String message_text = "";
    private String sip_uri = "robot@127.0.0.1:5060";
    private int sip_uri_port = 5060;
    private String sip_uri_user = "robot";
    private String sip_uri_host = "localhost";
    private int http_bind_port = 80;
    private String http_bind_host = "127.0.0.1";
    private static final String ip_pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5]))";
    private static final String SIP_USER_URI_PATTERN = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*";
    private static final String SIP_USER_HOST_PATTERN = "(([A-Za-z]+(\\.[A-Za-z0-9]+)*((\\.[A-Za-z]{2,})*?))|" + ip_pattern + ")";
    private static final String SIP_USER_PORT_PATTERN = "((:(\\d+?))??)";
    private static final String SIP_URI_PATTERN = "(" + SIP_USER_URI_PATTERN
            + "@"
            + SIP_USER_HOST_PATTERN
            + SIP_USER_PORT_PATTERN
            + ")";
    private static final String HTTP_BIND_PATTERN = "((((([A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))|" + ip_pattern + ")|" + ip_pattern + ")((:(\\d+?))??))|(\\d+?))";
    private static final String CONFIG_FILE_PATTERN = "((~)??((/|(\\.\\.)|([_A-Za-z0-9-]*?))*?[_A-Za-z0-9-]+?)(\\.cfg))";
    private static final String OPTIONS_PATTERN = "(^(\\s-c\\s" + CONFIG_FILE_PATTERN + ")??)" + "((\\s-user\\s" + SIP_URI_PATTERN + ")??)" + "((\\s-http\\s" + HTTP_BIND_PATTERN + ")??)$";
    private Pattern options_pattern;
    private static final String CONF_FILE_PATTERN = "([_A-Za-z0-9-]+?)(\\.cfg)";
    private static final String WAV_FILE_PATTERN = "([_A-Za-z0-9-]+?)(\\.wav)";
    private Pattern pattern;

    public String getConfig_file_name() {
        return config_file_name;
    }

    public void setConfig_file_name(String config_file_name) {
        this.config_file_name = config_file_name;
    }

    public String getDefault_message() {
        return default_message;
    }

    public void setDefault_message(String default_message) {
        this.default_message = default_message;
    }

    public String getHttp_bind_host() {
        return http_bind_host;
    }

    public void setHttp_bind_host(String http_bind_host) {
        this.http_bind_host = http_bind_host;
    }

    public int getHttp_bind_port() {
        return http_bind_port;
    }

    public void setHttp_bind_port(int http_bind_port) {
        this.http_bind_port = http_bind_port;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getMessage_wav() {
        return message_wav;
    }

    public void setMessage_wav(String message_wav) {
        this.message_wav = message_wav;
    }

    public String getSip_uri() {
        return sip_uri;
    }

    public void setSip_uri(String sip_uri) {
        this.sip_uri = sip_uri;
    }

    public String getSip_uri_host() {
        return sip_uri_host;
    }

    public void setSip_uri_host(String sip_uri_host) {
        this.sip_uri_host = sip_uri_host;
    }

    public int getSip_uri_port() {
        return sip_uri_port;
    }

    public void setSip_uri_port(int sip_uri_port) {
        this.sip_uri_port = sip_uri_port;
    }

    public String getSip_uri_user() {
        return sip_uri_user;
    }

    public void setSip_uri_user(String sip_uri_user) {
        this.sip_uri_user = sip_uri_user;
    }

    @Override
    public String toString() {
        return ("============== conf ==============\n"
                + "config_file_name: " + config_file_name + "\n"
                + "sip_uri_port: " + sip_uri_port + "\n"
                + "sip_uri_user: " + sip_uri_user + "\n"
                + "sip_uri_host: " + sip_uri_host + "\n"
                + "http_bind_port: " + http_bind_port + "\n"
                + "http_bind_host: " + http_bind_host + "\n"
                + "default_message: " + default_message + "\n"
                + "message_wav: " + message_wav + "\n"
                + "message_text: " + message_text + "\n"
                + "==================================");

    }

    public void conf_check_parameters() {
        pattern = Pattern.compile(CONF_FILE_PATTERN);
        if (!pattern.matcher(config_file_name).matches()) {
            System.out.println("Error in the configuration file name.\n It should respect the format: name.cfg\n Default value will be use");
            config_file_name = "default.cfg";
        }
        pattern = Pattern.compile(WAV_FILE_PATTERN);
        if (!pattern.matcher(default_message).matches()) {
            System.out.println("Error in the configuration default message file name.\n It should respect the format: name.wav\n Default value will be use");
            default_message = "default.wav";
        }
        if (!pattern.matcher(message_wav).matches()) {
            System.out.println("Error in the configuration default message file name.\n It should respect the format: name.wav\n Default value will be use");
            default_message = "current.wav";
        }
        pattern = Pattern.compile(SIP_URI_PATTERN);
        if (!pattern.matcher(sip_uri).matches() || sip_uri.equals("")) {
            System.out.println("Error or empty sip uri\n Default value will be use");
            sip_uri = "robot@127.0.0.1:5061";
        }
        pattern = Pattern.compile(SIP_USER_HOST_PATTERN);
        if (!pattern.matcher(sip_uri_host).matches() || sip_uri_host.equals("")) {
            System.out.println("Error or empty sip host\n Default value will be use");
            sip_uri_host = "127.0.0.1";
        }
        if (sip_uri_user.equals("")) {
            System.out.println("empty sip user\n Default value will be use");
            sip_uri_user = "robot";
        }
        if (http_bind_host.equals("")) {
            System.out.println("empty http host\n Default value will be use");
            http_bind_host = "127.0.0.1";
        }

        if (message_wav.equals("")) {
            System.out.println("empty message_wav\n Default value will be use");
            message_wav = "current.wav";
        }
    }

    public conf_reader(String[] args) {
        this.args = args;
        options_pattern = Pattern.compile(OPTIONS_PATTERN);
        int i = 0;
        for (String s : args) {
            command_line = command_line + " " + s;
        }

    }

    private void readConfig_file() {

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(config_file_name));
            if (prop.containsKey("default_message") & !prop.getProperty("default_message", "").equals("")) {
                setDefault_message(prop.getProperty("default_message", "default.wav").replaceAll(" ", ""));
            }
            if (prop.containsKey("message_wav") & !prop.getProperty("message_wav", "").equals("")) {
                setMessage_wav(prop.getProperty("message_wav", "").replaceAll(" ", ""));
            }
            if (prop.containsKey("message_text") & !prop.getProperty("message_text", "").equals("")) {
                setMessage_text(prop.getProperty("message_text", ""));
            }
            if (prop.containsKey("http_interface") & !prop.getProperty("http_interface", "").equals("")) {
                setHttp_bind_host(prop.getProperty("http_interface", http_bind_host).replaceAll(" ", ""));
            }
            if (prop.containsKey("sip_interface") & !prop.getProperty("sip_interface", "").equals("")) {
                setSip_uri_host(prop.getProperty("sip_interface", sip_uri_host).replaceAll(" ", ""));
            }
            try {
                if (prop.containsKey("sip_port") & !prop.getProperty("sip_port", "").equals("")) {
                    setSip_uri_port(Integer.parseInt(prop.getProperty("sip_port", "").replaceAll(" ", "")));
                }
                if (prop.containsKey("http_port") & !prop.getProperty("http_port", "").equals("")) {
                    setHttp_bind_port(Integer.parseInt(prop.getProperty("http_port", "").replaceAll(" ", "")));
                }
            } catch (NumberFormatException e) {
                System.out.println("Error in the conf file");

            }
            if (prop.containsKey("sip_user") & !prop.getProperty("sip_user", "").equals(""))
                setSip_uri_user(prop.getProperty("sip_user", sip_uri_user).replaceAll(" ", ""));

        } catch (IOException ex) {
            System.out.println("Error file not found");

        }
    }

    public boolean check() {
        if (options_pattern.matcher(command_line).matches()) {
            return true;
        } else {
            System.out.println("OPTIONS: ERROR");
            System.out.println("USAGE: java SIPSpeaker [-c config_file_name.cfg] [-user user@host[:port]] [-http host[:port]] ");
            return false;
        }
    }

    public boolean parsing() {
        int i = 0;
        while (i < args.length) {
            String arg = args[i];
            String value = args[i + 1];
            i = i + 2;
            if (arg.equals("-c")) {
                setConfig_file_name(value);
                readConfig_file();
            } else if (arg.equals("-user")) {
                String user = value.substring(0, value.indexOf("@"));
                setSip_uri(value);
                String host;
                int port;
                int portseparator = value.indexOf(":");
                if (portseparator == -1) {
                    host = value.substring(value.indexOf("@") + 1, value.length());
                } else {
                    host = value.substring(value.indexOf("@") + 1, portseparator);
                    port = Integer.parseInt(value.substring(portseparator + 1, value.length()));
                    setSip_uri_port(port);
                }
                setSip_uri_host(host);
                setSip_uri_user(user);

            } else if (arg.equals("-http")) {
                String host;
                int port;
                int portseparator = value.indexOf(":");
                if (portseparator == -1) {
                    host = value.substring(0, value.length());
                } else {
                    host = value.substring(0, portseparator);
                    port = Integer.parseInt(value.substring(portseparator + 1, value.length()));
                    setHttp_bind_port(port);
                }
                setHttp_bind_host(host);
            } else {
                System.out.println("Error");
                return false;
            }           
            
        }
               
        sip_uri = sip_uri_user + "@" + sip_uri_host + ":" + sip_uri_port;
        sip_uri= sip_uri.replaceAll(" ", "");
        return true;
    }
}
