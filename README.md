Bioinformatic TP - UTN FRBA
=================================

## Getting Started

### Dependencies:

* Java 1.8
* Scala 2.12.2
* SBT 0.13.13

### Generate a Fat Jar with Assembly:

* Run `sbt assembly` from the project root.

The fully executable JAR will be in `/target/scala-2.12/` ready to rock.

### Running the Script

The following is an example to run the app:

```
java -jar ./target/scala-2.12/bio-tp.jar
    transcription
    --input /home/cristian/Documents/Development/bio/bio-tp/src/main/resources/sourcing/INS_mRNA_NM_000207.gb
    --output /home/cristian/Documents/Development/bio/bio-tp/src/main/resources/output/fasta/exc1.fas
```
