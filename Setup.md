Twister DACIDR Setup
==
This document will guide you in setting up the environment required to run applications in the Tister-DACIDR package.

Prerequisites
--
1. Operating System
  * This program is extensively tested and known to work on,
    *  Red Hat Enterprise Linux Server release 5.10 (Tikanga)
    *  Cray Linux Environment (based on SUSE Linux SLES 11) in Cluster Compatibility Mode (CCM)
    *  Ubuntu 12.10
  * There is no restriction to run this on Windows systems, but an earlier experiment showed us it does not perform well, hence we recommend using a Linux based system instead.

2. Java
  * Download Oracle JDK 8 from http://www.oracle.com/technetwork/java/javase/downloads/index.html
  * Extract the archive to a folder named `jdk1.8.0`
  * Set the following environment variables.
  ```
    JAVA_HOME=<path-to-jdk1.8.0-directory>
    PATH=$JAVA_HOME/bin:$PATH
    export JAVA_HOME PATH
  ```

3. Apache Ant
  * Download latest Ant release from http://ant.apache.org/bindownload.cgi
  * Extract it to some folder and set the following environment variables.
  ```
    ANT_HOME=<path-to-Ant-folder>
    $PATH=$ANT_HOME/bin:$PATH
    export ANT_HOME PATH
  ```
  
4. Apache ActiveMQ
  * Download Apache ActiveMQ 5.10.0 release from http://activemq.apache.org/activemq-5100-release.html
  * Extract it to some folder and set the following environment variables.
  ```
    ACTIVEMQ_HOME=<path-to-ActiveMQ-folder>
    $PATH=$ACTIVEMQ_HOME/bin:$PATH
    export ACTIVEMQ_HOME PATH
  ```
  
5. Twister
  * Download Twister iterative MapReduce framework release from http://www.iterativemapreduce.org/download.html
  * Extract it to some folder and set the following environment variables.
  ```
    TWISTER_HOME=<path-to-TWISTER-folder>
    $PATH=$TWISTER_HOME/bin:$PATH
    export TWISTER_HOME PATH
  ```  
  
Building Twister-DACIDR
--
* Check all prerequisites are satisfied before building dapwc
* Clone this git repository from `git@github.com:DSC-SPIDAL/Twister-DACIDR.git` Let's call this directory `DACIDR_HOME` by exporting the following environment variable
  ```
    DACIDR_HOME=<path-to-Twister-DACIDR-git-clone>
    export DACIDR_HOME
  ```
  
* Once above two steps are completed, building Twister-DACIDR requires only one command, `ant`, issued within `DACIDR_HOME`.
* Copy the jar files in `$DACIDR_HOME/lib` to `$TWISTER_HOME/lib`
  ```
    cp $DACIDR_HOME/lib/*.jar $TWISTER_HOME/lib
  ```
  
* Copy `$DACIDR_HOME/build/twister-dacidr.jar` to `$TWISTER_HOME/apps`
  ```
    cp $DACIDR_HOME/build/twister-dacidr.jar $TWISTER_HOME/apps
  ```

