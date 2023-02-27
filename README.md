# suffix-array

A program to build a suffix array (using 3rd party [carrotsearch/jsuffixarrays](https://github.com/carrotsearch/jsuffixarrays) - more info [here](http://labs.carrotsearch.com/jsuffixarrays.html)) and to perform queries on the suffix array.

This program is written in Java. 

## Running buildsa and querysa
Recommended steps to run the program:

1. Check out the code from Github.
2. Import buildsa and querysa into Eclipse as Maven projects.
3. Run `mvn clean install` in Eclipse to clean and build the projects.
4. Run buildsa as a Java application in Eclipse with program arguments: `--preftab <k> reference output`. For example, `--preftab 2 genomeData/virusGenome.fna ../output/virusGenome.bin`
5. Run querysa as a Java application in Eclipse with program arguments: `index queries queryMode output`. For example, `../output/virusGenome.bin ../queries/query.fna SIMPACCEL ../output/queryOutput.txt`

Alternatively, the jar should be able to be run via `java -jar buildsa.jar --preftab <k> reference output`.  The executable jar file `querysa.jar` should be able to be run via `java -jar querysa.jar index queries queryMode output`.  

## Resources
For file serialization, I consulted [this document](http://www.math.uaa.alaska.edu/~afkjm/csce222/handouts/FileBinarySerialization.pdf) from the University of Alaska.

For information about FASTA files and how to parse them, I consulted [Wikipedia](https://en.wikipedia.org/wiki/FASTA_format) and [Rosetta Code](https://rosettacode.org/wiki/FASTA_format#Java).

For building a multi-module Maven project, I consulted [this webpage](https://www.baeldung.com/maven-multi-module).

For a Maven pom template example, I consulted [this webpage](https://www.tutorialworks.com/maven-pom-template/).

I consulted StackOverflow for a few simple questions, like how to best parse integers as Strings, and examples of Enum classes.
