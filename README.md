[![Coverage Status](https://coveralls.io/repos/github/agreementmaker/agreementmaker/badge.svg?branch=master)](https://coveralls.io/github/agreementmaker/agreementmaker?branch=master)
![NCSA](https://img.shields.io/badge/license-NCSA-green.svg)

# AgreementMaker Ontology Matching System

AgreementMaker is an ontology matching system was started by the ADVIS Laboratory
at the University of Illinois at Chicago, under the supervision of [Professor Isabel F. Cruz](http://www.cs.uic.edu/Cruz/).

It has competed multiple times in the [Ontology Alignment Evaluation Initiative](http://oaei.ontologymatching.org/) 
and presented impressive results.

## Project Organization

AgreementMaker is split into multiple projects, all in the `projects` sub-directory.
To build Agreementmaker, use Gradle:

```shell script
./gradlew build
```

## Running

### WordNet Dictionary Files

AgreementMaker requires the WordNet dictionary files at application startup.  If it cannot find them it will show an error like this:

```
ERROR WordNetUtils java.lang.RuntimeException: Cannot find WordNet dictionary files. Expected the files to exist in this directory: /path/to/agreementmaker/AM_ROOT/wordnet-3.0
```

NOTE: On your computer the path `/path/to/agreementmaker/AM_ROOT/wordnet-3.0` will be a real path.

You can ensure that the files are there by downloading them from the [WordNet website](https://wordnet.princeton.edu/download/current-version) (substitute `/path/to/agreementmaker/AM_ROOT/wordnet-3.0` with the real path in the error message):

```
mkdir -p /path/to/agreementmaker/AM_ROOT/wordnet-3.0
cd /path/to/agreementmaker/AM_ROOT/wordnet-3.0
wget http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz
tar --strip-components=1 -xvzf wn3.1.dict.tar.gz
```

To run AgreementMaker, use the `run` Gradle task:

```shell script
./gradlew run
```

## Sample Ontologies

We can use the [OAEI 2012 ontology dataset](http://oaei.ontologymatching.org/2012/benchmarks/benchmarks.zip) for matching from the [2012 OAEI Campaign](http://oaei.ontologymatching.org/2012/benchmarks/index.html#datasets) page.

1. Download and unzip the `benchmarks.zip`.
2. Start AgreementMaker with `./mvnw pax:provision`.

In AgreementMaker:

3. Navigate to `File -> Open Ontologies... (Ctrl + O)`.
4. In the `Source Ontology` tab, check the `Load Ontology` box, select the `benchmarks/101/onto.rdf` file, and set the language to `OWL`.
5. In the `Target Ontology` tab, check the `Load Ontology` box, select the `benchmarks/201/onto.rdf` file, and set the language to `OWL`.
6. Click `Proceed`, and `Ok` on the dialogs.

Now to match the ontologies:

7. Click the `Match!` button in the `Matching Tasks Control Panel`.
8. Notice that `Base Similarity Matcher` is selected in the `Matching Algorithm` tab.
9. Click `Run Matching Task` to match the ontologies.

