=======================================================================
============================ Compile & Run ============================
=======================================================================
First, you need to have freetts and JMF on your computer.

To compile run on the src folder:
	javac *.java 

To launch run on the src folder:
	java SIPSpeaker [-c config_file_name] [-user user@host[:port]] [-http host[:port]] 


=======================================================================
============================ Web interface ============================
=======================================================================

To have access to the web server in order to change the message, you have to go to:

      " http_host:port/formtts.html "

(for instance localhost:80/formtts.html)

In this web page you can change/remove the current message


=======================================================================
=========================== Call SIPSpeaker ===========================
=======================================================================

To call the SIPSpeaker you need to use the uri of the SIPSpeaker.
If you make a mistake in the user part, you will receive "number doesn't exist".

=======================================================================
============================== Conf file ==============================
=======================================================================

The conf file should respect the syntax explain in java.util.Properties.

Example:

# This is a comment. 

#

# The default message wave-file. If the user chooses to delete the current message a
# default should exist. If the file wav-file specified here for the default doesn't
# exist or if the configuration file doesn't exist a default file should be created
# on start of the application. Generating a default file dynamically is only required
# for grade 5. The message in the default file should be hard coded in the application.

default_message = default.wav

# Current message (message_wav and message_text)

# The current message is the message that was set by the web configuration interface
# and should be saved when the application exits. If the current message is deleted the default
# should be played.

message_wav = currentmessage.wav

message_text = Welcome to the SIP Speaker this is my own answering machine. You have no new messages.

# Date in seconds according constructor of java.util.Date(long date). This is optional to
# implement, but if implemented use this name

message_recived = 43534522135 

 

# If the line is empty use default 

# Bellow is the interface that the sip server part should bind and listen to. To listen to all
# existing interfaces use the interface 0.0.0.0. (This is true for the web server as well.) 

sip_interface =

sip_port = 5060 

sip_user = robot 

# HTTP web server

http_interface = 127.0.0.1

http_port = 80 
