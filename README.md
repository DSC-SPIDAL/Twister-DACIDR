## How to run DACIDR using Twister-0.9
1. Gain access to Linux environment machine
2. Set up Twister-0.9 environment following the steps from: [http://www.iterativemapreduce.org/]
3. Export the classes under dacidr into a jar file (use ANT), and put it under $TWISTER_HOME/apps
4. Copy all the files under lib folder except the Twister-0.9.jar and put it under $TWISTER_HOME/lib
5. run the application

See the documentations on: [http://salsahpc.indiana.edu/millionseq/]

##The applications in DACIDR
There are 3 applications under DACIDR, All-pair Sequence Alignment (ASA), Multidimensional Scaling (MDS) and Interpolation.
* ASA is under package cgl.imr.samples.dacidr.pwa
* MDS is under package cgl.imr.samples.dacidr.wdasmacof
* Interpoaltion is under package cgl.imr.samples.dacidr.inter

The scripts of running these applications are under script folder

1. Running ASA
The normal script of running ASA is pwaMul.sh

```
arguments: [num_of_map_tasks] [num_of_reduce_tasks] [sequence_count] 
[num_of_partitions] [data_dir] [gene_block_prefix] [tmp_output_prefix] 
[output_map_file] [aligner type] [score matrix type] [sequence type]
```

2. Running MDS
The normal script of running MDS is run_dasmacof_cg_mem.sh

```
Usage: [1. Num map tasks ] [2. Input Folder] [3. Input File Prefix] [4. Input Weight Prefix] [5. IDs File ] 
        [6. Label Data File ] [7. Output File ] [8. Threshold value ] [9. The Target Dimension ] 
        [10. Cooling parameter (alpha) ] [11. Input Data Size] [12. Final Weight Prefix] 
        [13. CG iteration num] [14. CG Error Threshold]
```

##TWISTER SETUP!!
The Twister Pipeline is used to process genomic sequence information on a computer cluster.

#####Follow steps on http://www.iterativemapreduce.org/userguide.html
* Do not use NaradaBrokering
* Use ActiveMQ broker instead (likely have to install)
* Any steps involving NaradaBrokering, skip or do with AMQ.
* IP address needs to be changed in appropriate files every time this is run from new nodes/computer:
* Update the compute nodes under $TWISTER_HOME/bin/nodes
* Update brokder url in amq.properties in $TWISTER_HOME/bin
* Make sure other file locations are correct in files mentioned in the userguide above

#####Must Have These
* Download ANT from Apache - Used to compile .jar files (JAVA)
* Move activemq.....jar file to twister-0.9/lib/
* Set up environmental variables in .bashrc so that you don't have to do it
* Copy the scripts to the head node

#####Update environment variables
* TWISTER_HOME
* ANT_HOME
* JAVA_HOME
* AMQ_HOME (check .bashrc file to see if environmental variable is
* AMQ_HOME or ACTIVEMQ_HOME if it already exists)

#####Optional: Download JAVA JDK in order to have original JDK as a backup.
* Start ActiveMQ: AMQ_HOME/bin/activemq console
* Run start_twister.sh from TWISTER_HOME/bin
* ActiveMQ Broker is only run on one node, or on a seperate machine in cases of huge data sets.

## RUNNING Smith-Waterman (Pairwise Sequence Alignment)

#####Script Names:
* pwaFileSpliter.sh function splits sequence file into smaller partitions
* pwaMul.sh function performs distance matrix calculations - resulting files are in directory specified when prior functions are run.

```
[lsaggu@i97 dacidr]$ ./pwaFileSpliter.sh args: [gene_seq_file] [sequence_count] 
[num_of_partitions] [out_dir] [gene_block_prefix] [output_idx file] [Alphabet]

[lsaggu@i97 dacidr]$ ./pwaFileSpliter.sh ~/data/test/4640_fasta.txt 4640 16 ~/data/test/16/ input_ ~/data/test/4640_16.idx RNA
```
1. num_of_partitions = number of cores....? Partitions gene sequence files into more manageable sized units
2. num_of_partitions = number of cores (nodes*cores) Don't include head node Partitions gene sequence files into more manageable sized units
3. out_dir = directory to output files
4. gene_block_prefix = prefix before file name (i.e. input_???)
5. output_idx file = location/name of file to store output idx....
6. Alphabet = alphabet to use to read sequences: 'RNA' in most cases.

```
[lsaggu@i97 dacidr]$ ./pwaMul.sh

THis generates pid_ as well
args: [num_of_map_tasks] [num_of_reduce_tasks] [sequence_count] [num_of_partitions] 
[data_dir] [gene_block_prefix] [tmp_output_prefix] [aligner type]
[score matrix type] [sequence type]

[lsaggu@i97 dacidr]$ ./pwaMul.sh 16 4 4640 16 ~/data/test/16/ input_ swg_ SWG edn RNA
```
1. num_of_map_tasks = number of cores (nodes*ppn) Don't include head node
2. num_of_reduce_tasks = number of nodes (Don't include head node)
3. sequence_count = number of sequences
4. num_of_partitions = number of cores
5. data_dir = directory in which data was stored (same as out_dir from pwaFileSplitter)
6. gene_block_prefix = prefix before file name (same as from pwaFileSplitter)
7. tmp_output_prefix = prefix for output files (used in run_dasmacof_cg_mem.sh)
8. aligner type = SWG or NW
9. score matrix type = edn or blo
10. sequence type = RNA or DNA (DNA has reverse scores)

##RUNNING MDS
#####Script Names:
* randomeWeights.sh - Generate a random weights output folder.
* mdsFileSplitter.sh - Split the matrix file
* run_dasmacof_cg_mem.sh - run MDS algorithm

#####RUN RANDOM WEIGHTS (randomeWeights.sh)
In Twister_HOME directory, go to samples/dacidr - Weights indicate significance of certain data points
```
randomWeights.sh [1. output weighted matrix] [2. row] [3. col]
[4. percentage] [5. symmetric (0:no; 1:yes)] [6. weight value]
```
1. where output weight matrix directory will be.
2. number of sequences
3. number of sequences
4. percentage of points to be given weight of 0 (typically 0)
5. 0
6. 1

#####SPLIT WEIGHTS (mdsFileSplitter.sh)

```
mdsFileSplit.sh [1. Data File ] [2. Temporary directory to split data ]
[3. Temp file prefix ] [4. Output IDs file ] [5. Num map tasks ] [6. row size ] [7. column size] [8. Type of input value format (0: short; 1: double)]
```
1. output weight file from randomweights.sh
2. directory to store split data (will be same as input directory for MDS)
3. given by user: can be anything (weights_all1_)
4. output idx file: same as pwaFileSpliter
5. number of cores (ppn * nodes)
6. number of sequences
7. number of sequences
8. weight and distance matrices are in short (0)

#####RUN MDS (run_dasmacof_cg_mem.sh)

```
run_dasmacof_cg_mem.sh [1. Num map tasks ] [2. Input Folder]
[3. Input File Prefix] [4. Input Weight Prefix] [5. IDs File ] 
[6. Label Data File ] [7. Output File ] [8. Threshold value ] 
[9. The Target Dimension ] [10. Cooling parameter (alpha) ] 
[11. Input Data Size] [12. Final Weight Prefix] [13. CG iteration num] [14. CG Error Threshold]
```

1. The number of cores running job (nodes * ppn)
2. Output folder of pwaMul.sh (Distance files)
3. <Output prefix from pwaMul>_pid_
4. same as temp file prefix from mdsFileSplit
5. same as output IDs file from mdsFileSplit
6. "NoLabel"
7. Where output will go.
8. 0.000001 (resolution)
9. 3 (3D)
10. 0.95 (0 for EM-SMACOF)
11. number of sequences
12. Same as 4
13. 20
14. 1

###Detailed Steps (Update based on the notes taken down here:
[https://github.com/lsaggu/cloudmesh_pbs/blob/master/doc/twisterPipelineSetup.rst]

###Paper References
1. Yang Ruan, Geoffrey House, Saliya Ekanayake, Ursel Schütte, James D. Bever, Haixu Tang, Geoffrey Fox. **Integration of Clustering and Multidimensional Scaling to Determine Phylogenetic Trees as Spherical Phylograms Visualized in 3 Dimensions.** Proceedings of C4Bio 2014 of IEEE/ACM CCGrid 2014, Chicago, USA, May 26-29, 2014.
2. Stanberry, Larissa, Roger Higdon, Winston Haynes, Natali Kolker, William Broomall, Saliya Ekanayake, Adam Hughes et al. **"Visualizing the protein sequence universe."** Concurrency and Computation: Practice and Experience 26, no. 6 (2014): 1313-1325.
3. Yang Ruan, Geoffrey Fox. **A Robust and Scalable Solution for Interpolative Multidimensional Scaling with Weighting.** Proceedings of IEEE eScience 2013, Beijing, China, Oct. 22-Oct. 25, 2013. *(Best Student Innovation Award)*
4. Yang Ruan, Saliya Ekanayake, Mina Rho, Haixu Tang, Seung-Hee Bae, Judy Qiu, Geoffrey Fox. **DACIDR: Deterministic Annealed Clustering with Interpolative Dimension Reduction using a Large Collection of 16S rRNA Sequences.** Proceedings of ACM-BCB 2012, Orlando, Florida, ACM, Oct. 7-Oct. 10, 2012.
5. Yang Ruan, Zhenhua Guo, Yuduo Zhou, Judy Qiu, Geoffrey Fox. **HyMR: a Hybrid MapReduce Workflow System.** Proceedings of ECMLS’12 of ACM HPDC 2012, Delft, Netherlands, ACM, Jun. 18-Jun. 22, 2012
6. Adam Hughes, Yang Ruan, Saliya Ekanayake, Seung-Hee Bae, Qunfeng Dong, Mina Rho, Judy Qiu, Geoffrey Fox. **Interpolative Multidimensional Scaling Techniques for the Identification of Clusters in Very Large Sequence Sets.** BMC Bioinformatics 2012, 13(Suppl 2):S9.
7. Chris Hemmerich, Adam Hughes, Yang Ruan, Aaron Buechlein, Judy Qiu, Geoffrey Fox. **Map-Reduce Expansion of the ISGA Genomic Analysis Web Server.** CloudCom 2010. Indianapolis, IN.
