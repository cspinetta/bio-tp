Bioinformatic TP - UTN FRBA
=================================

## Getting Started

### Dependencies:

* [Java 1.8]
* [Scala 2.12.2]
* [SBT >= 0.13.13]

### Running as a Script

You only need `Java SE 1.8` to run the release. 

* Translation from RNA to the corresponding peptide sequences: 
```
java -jar ./releases/bio-tp-v1.0.jar \
    transcription \
    --input ./releases/INS_mRNA_NM_000207.gb \
    --output ./target/output/exc1.fas
```

* Search for alignment through NCBI QBlast service: 
```
java -jar ./releases/bio-tp-v1.0.jar \
    alignment \
    --input ./target/output/exc1.fas \
    --output ./target/output/exc2.fas
```

### Building the app

You need to generate a Fat Jar with Assembly:

* Run `sbt assembly` from the project root.

The fully executable JAR will be in `/target/scala-2.12/` ready to rock.


[Java 1.8]:http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[Scala 2.12.2]:https://www.scala-lang.org/download/2.12.2.html
[SBT >= 0.13.13]:http://www.scala-sbt.org/download.html
