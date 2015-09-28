Observer
========

This is a Java program employing SeleritySync API to listen for and
enrich Selerity's observations.


Required Software
=================

To build the program the following are required:

   compatible with Java 1.7 and Java 1.8 SE  (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
   Ant 1.8.2 (http://ant.apache.org/bindownload.cgi)
   Ant-JUnit 1.8.2 (bundled with Ant package on Apache website; separate installation may be required if installing using OS package manager)

Both Java and Ant should be installed and in your command path, and JAVA_HOME should
be set.


Required Access
===============

The program requires SeleritySync API access to Selerity's servers. If you do not 
have this set up contact support@seleritycorp.com.


Building
========

In a command shell:

   ant test

A successful build concludes:

  BUILD SUCCESSFUL


Running
=======

In a command shell:

   ant dist-jar
   java -jar dist/observer.jar

This program has been run successfully on, but is not necessarily limited to, Mac
OS X, Linux, and Windows Server 2008.


Setting up client certificate
=============================

In order to be able connect to Selerity's public endpoints, you have
to configure Java to send along your client certificate (If you
haven't received a client certificate, please let us know. See
"Support" below).

To configure Java to send along your client certificate, please set up
a KeyStore and import your client certificate there. You can find the
details in your Java documentation.

For development purposes, you can dodge KeyStore-setup and instead
simply add

  -Djavax.net.ssl.keyStoreType=pkcs12
  -Djavax.net.ssl.keyStore=/path/to/your/certificate.p12
  -Djavax.net.ssl.keyStorePassword="YourCertificatePassword"

as first arguments to every call of the Java executable.


Configuring
===========

Observer is configured with a properties file.  A default set of
properties is included in the jar (See example in resources) which
documents the settings available. To provide you own configuration
simple copy the sample, and update it and then indicate the new file
on the command line:

   java -Dconfig.properties=my.properties -jar dist/observer.jar

Logging is configured using the standard java logging properties file
which can be retrieved from a file or a jar resource (See example in
resources) as follows:

   java -Djava.util.logging.config.class=com.seleritycorp.cs.standalone.commons.logging.LoggingConfig -Djava.util.logging.config.file=logging.properties -jar dist/observer.jar


Observation Processors
======================

Once this program gets a valid observation and enriches it, the
EnrichedObservation is sent to a Processor. Any Processor can be
designated in the properties.  To change how the program processes
observations simply implement a new Processor subclass and indicate it
in the properties.

Support
=======

Selerity's contact info is found on our website http://www.seleritycorp.com
or you can simply email us at support@seleritycorp.com.


Change Log
==========

20120328:
- Pre-load obs specs and reference data to avoid on the fly lookups.
- Improve filename generation to avoid appending two records in a file.
- Log message format to files improved.

20120419:
- Cleaned up build.xml descriptions
- Reworked reconnect logic

20120420:
- Fixed edge case in reconnect logic.

20150925:
- Update sample config to use rted-api endpoints

20150928:
- Document requirement of client certificate