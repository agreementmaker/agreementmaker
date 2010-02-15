////********** ________AGREEMENTMAKER_________ *****************/////////

1) Extract the AgreementMaker distribution folder in any location

2) Run the system
WINDOWS: double click on AgreementMaker\agreementMaker-windows-2GB.bat 
UNIX: run AgreementMaker\agreementMaker-linux-2GB.sh
The AgreementMaker application window will be shown.

3) Load source and target ontologies from File menu.
You can load you own ontologies, or using sample ontologies contained in the directory:
AgreementMaker\Ontologies.

4) Run and manage several matchings, start with Base_similarity and Parametric String Matcher.

5) Evaluate them through the reference file contained in the ontologies directory.

6) Save alignments found into a file

For more information you can access the Help through the Help menu.


////********** Sample Ontologies *****************/////////

A brief description of the sample ontologies of the AgreementMaker

*******************I3CON2004 ONTOLOGIES

Language: OWL
Format: N3
For each test-case contains a source ontology (e.g. WeaponsA)
and a target ontology (e.g. WeaponsB)
the reference alignment is contained in the file WeaponsAB.
Those ontologies are very similar therefore easy to be aligned.

********************OAEI 2098 ONTOLOGIES

This group contains several testcases for more information visit:
http://oaei.ontologymatching.org/2009/

Language: OWL
Format: RDF/XML
for each test case use 
101.rdf as source ontology
several target ontologies can be used
from 102.rdf to 301.rdf and for each ontology there is the corrisponding alignment (e.g. 101-301.rdf)
Most interesting testcases with real world ontologies are 101-301 101-302 101-303 101-304
On the website there is a detailed description of each testcase.


***************GEOSPATIAL ONTOLOGIES

Are XML ontologies 

the wetland testcase is quite simple.

The medison-dane testcase is very complicated.
source is madison and dane is target.
the reference alignment is in the last of the available formats