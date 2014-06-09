How to run DACIDR using Twister-0.9
======
1) Gain access to Linux environment machine
2) Set up Twister-0.9 environment following the steps from: http://www.iterativemapreduce.org/
3) Export the classes under dacidr into a jar file, and put it under $TWISTER_HOME/apps
4) Copy all the files under lib folder except the Twister-0.9.jar and put it under $TWISTER_HOME/lib
5) run the application

The applications in DACIDR
======
There are 3 applications under DACIDR, All-pair Sequence Alignment (ASA), Multidimensional Scaling (MDS) and Interpolation.
ASA is under package cgl.imr.samples.dacidr.pwa
MDS is under package cgl.imr.samples.dacidr.wdasmacof
Interpoaltion is under package cgl.imr.samples.dacidr.inter

The scripts of running these applications are under script folder

1) Running ASA
The normal script of running ASA is pwaMul.sh


2) Running MDS
The normal script of running MDS is run_dasmacof_cg_mem.sh
