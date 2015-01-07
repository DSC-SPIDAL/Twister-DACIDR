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

Starting Twister-DACIDR
--
A set of nodes running Twister is required to run any of the applications in Twister-DACIDR. We will assume that you already have access to some nodes and `importantly` passwordless SSH is enabled between them. Further, we assume these nodes have a shared filesystem. This is not a mandatory requirement, but otherwise it would require manual copying of files to all nodes to get Twister working properly.

* We will dedicate one of the nodes, say `broker-node`, to run the ActiveMQ broker, so list all the node IPs except the `broker-node` in a file named `nodes`. Then copy this file to `$TWISTER_HOME/bin`
* SSH to `broker-node` in a separate terminal and start the ActiveMQ broker as follows.
  ```
    cd $ACTIVEMQ_HOME/bin
    ./activemq console
  ```

* Set `broker-node` IP in `$TWISTER_HOME/bin/amq.properties` file as shown below.
  ```
    uri=failover\:(tcp\://broker-node\:61616)
  ```
  
* Configure properties in `$TWISTER_HOME/bin/twister.properties` as shown below. Change `#sockets`, `#cores`, and `path-to-TWISTER_HOME` with appropriate values. `#sockets` means the number of physical processors in a node and `#cores` means the number of cores in a processor.
  ```
    daemon_port=12500
    daemons_per_node=#sockets
    workers_per_daemon=#cores
    pubsub_broker=ActiveMQ
    app_dir=path-to-TWISTER_HOME/apps
    data_dir=path-to-TWISTER_HOME/data
    nodes_file=path-to-TWISTER_HOME/nodes
  ```
  
* Set maximum memory for a Twister daemon as `total memory per node / daemons per node` in `$TWISTER_HOME/bin/stimr.sh` `-Xmx` parameter

* Finally, start Twister using,
  ```
    cd $TWISTER_HOME/bin
    ./start_twister.sh
  ```
  
 * You can now proceed to running applications in Twister-DACIDR in a separate terminal following the README guide in this repository.
