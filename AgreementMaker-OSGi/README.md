# AgreementMaker

AgreementMaker is an ontology matching and visualization tool.
It also serves as a platform to implement and test new ontology 
matching algorithms.

# AgreementMaker-OSGi

This is an OSGi version of AgremeentMaker.  All the sub-directories are each an
OSGi bundle.

## Building

AgreementMaker uses the Maven build system, with [maven-wrapper](https://github.com/takari/maven-wrapper).  To build it type:

    ./mvnw install

in the `AgreementMaker-OSGi` directory.

Run the bundles with your favorite OSGi implementation (for example, [Apache Felix](http://felix.apache.org/)).  To see how to run AgreementMaker in an IDE, please refer to the [wiki](https://github.com/agreementmaker/agreementmaker/wiki).

## Running

After `./mvnw install`, start AgreementMaker with:

    ./mvnw pax:provision

