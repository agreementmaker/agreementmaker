[![Coverage Status](https://coveralls.io/repos/github/agreementmaker/agreementmaker/badge.svg?branch=master)](https://coveralls.io/github/agreementmaker/agreementmaker?branch=master)
![GNU Affero GPL v3](https://img.shields.io/badge/license-Affero%20GPL%20v3-blue.svg)

# AgreementMaker Ontology Matching System

AgreementMaker is an ontology matching system was started by the ADVIS Laboratory
at the University of Illinois at Chicago, under the supervision of [Professor Isabel F. Cruz](http://www.cs.uic.edu/Cruz/).

It has competed multiple times in the [Ontology Alignment Evaluation Initiative](http://oaei.ontologymatching.org/) 
and presented impressive results.

The currently supported version of AgreementMaker is under the [AgreementMaker-OSGi](AgreementMaker-OSGi) directory.

# Running

You can run AgreementMaker using the Maven Pax Runner plugin:

    cd AgreementMaker-OSGi
    ./mvnw install pax:provision

For Windows:

    cd AgreementMaker-OSGi
    ./mvnw.cmd install pax:provision


# Sample Ontologies

We can use the [OAEI 2012 ontology dataset](http://oaei.ontologymatching.org/2012/benchmarks/benchmarks.zip) for matching.
