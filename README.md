TP Bioinformatica - UTN FRBA
=================================

## Running as a Script

You only need [Java 1.8] to run the release. 

* Translation from mRNA to the corresponding protein sequence: 
```
java -jar ./releases/bio-tp-v1.0.jar \
    transcription \
    --input ./releases/SOD1_mRNA_NM_000454.gb \
    --output ./releases/result/protein_seq.fas
```

* Search for alignments via _NCBI QBlast service_: 
```
java -jar ./releases/bio-tp-v1.0.jar \
    alignment \
    --input ./releases/result/protein_seq.fas \
    --output ./releases/result/alignments_1.txt \
    --index 1
```

* Search for alignments via _Local DB_: 
```
java -jar ./releases/bio-tp-v1.0.jar \
    alignment local \
    --input ./releases/result/protein_seq.fas \
    --output ./releases/result/alignments_1.txt \
    --dbpath /path/to/db
```

** In order to search alignments with a local DB you need to have
 in your local `blastall` with `blastp` program installed and the
 `swissprot` DB with Blast formatted.

## Building the app

*Development dependencies:*

* [JDK 1.8]
* [Scala 2.12.2]
* [SBT >= 0.13.13]

You need to generate a Fat Jar with Assembly:

* Run `sbt assembly` from the project root.

The fully executable JAR will be in `/target/scala-2.12/` ready to rock.

## How to get Blast and the swissprot DB in your local

[Exposed by NCBI](https://blast.ncbi.nlm.nih.gov/Blast.cgi?PAGE_TYPE=BlastDocs&DOC_TYPE=Download)
```
```


[JDK 1.8]:http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[Java 1.8]:http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[Scala 2.12.2]:https://www.scala-lang.org/download/2.12.2.html
[SBT >= 0.13.13]:http://www.scala-sbt.org/download.html
