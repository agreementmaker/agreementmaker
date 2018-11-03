# AgreementMaker

AgreementMaker is an ontology matching and visualization tool.
It also serves as a platform to implement and test new ontology matching algorithms.

# AgreementMaker-OSGi

AgremeentMaker is split up into many modules, each contained in a sub directory of this directory.

## Building

AgreementMaker uses the [Apache Maven](https://maven.apache.org) build system, with [maven-wrapper](https://github.com/takari/maven-wrapper).  To build it type:

    ./mvnw install

in the `AgreementMaker-OSGi` directory.

## Running

The main entrypoint to AgreementMaker is currently in the `AgreementMaker-UIGlue` module, in the `am.ui.glue.StartAgreementMaker` class.  Navigate to that class and start the program from there using an IDE.

## Getting Wordnet

Some matching algorithms require a local copy of [WordNet 3.0](https://wordnet.princeton.edu).  You can get it from [the official site](http://wordnetcode.princeton.edu/3.0/).  Extract `WordNet-3.0.tar.bz2` and put the contents of the `dict` sub directory in `AM_ROOT/wordnet-3.0`.