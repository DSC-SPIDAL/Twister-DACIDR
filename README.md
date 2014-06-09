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

arguments: [num_of_map_tasks] [num_of_reduce_tasks] [sequence_count] [num_of_partitions] [data_dir] [gene_block_prefix] 
            [tmp_output_prefix] [output_map_file] [aligner type] [score matrix type] [sequence type]

2) Running MDS
The normal script of running MDS is run_dasmacof_cg_mem.sh

Usage: [1. Num map tasks ] [2. Input Folder] [3. Input File Prefix] [4. Input Weight Prefix] [5. IDs File ] 
        [6. Label Data File ] [7. Output File ] [8. Threshold value ] [9. The Target Dimension ] [10. Cooling parameter (alpha) ]
        [11. Input Data Size] [12. Final Weight Prefix] [13. CG iteration num] [14. CG Error Threshold]
