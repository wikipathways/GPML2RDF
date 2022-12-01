# GPML2RDF

GPML2RDF creates RDF for WikiPathways [1]. Use cases, examples SPARQL queries, downloads, etc,
are available from the WikiPathways RDF Portal: http://rdf.wikipathways.org/

## Installation

### Setup

Clone or download this repository, and then import as an "Existing Java Project" in Eclipse.

There are two dependencies. You can make them available to [Maven](https://maven.apache.org/) using the instructions below.

Start from the GPML2RDF folder for both the WikiPathways API client, and PathVisio Core

#### PathVisio Core

```shell
git clone https://github.com/egonw/pathvisio-1.git
cd pathvisio-1
git checkout -b bridgedb/version3 origin/bridgedb/version3
ant clean core.jar
mvn install:install-file -Dfile=modules/org.pathvisio.core.jar -DgroupId=org.pathvisio \
  -DartifactId=pathvisio-core -Dversion=3.4.0-bridgedb-3.0.1-1 -Dpackaging=jar
```

#### WikiPathways API client 

```shell
git clone https://github.com/wikipathways/wikipathways-api-client-java.git
cd wikipathways-api-client-java
cd org.wikipathways.client
mvn -Dmaven.test.failure.ignore=true clean install
```

### Compiling

There is a `pom.xml` and you can compile the code with:

```shell
cd WP2RDF
mvn clean install
```

A single archive with all dependencies can be created with:

```shell
mvn package
```

### Try different pathways

Add GPML file in resources directory --> Open WP2RDF class --> Change pathwayFile location


1.Waagmeester A, Kutmon M, Riutta A, Miller R, Willighagen EL, Evelo CT, Pico AR. Using the Semantic Web for Rapid Integration of WikiPathways with Other Biological Online Data Resources. PLOS Computational Biology. 2016 Jun;12(6):e1004989+. Available from: http://dx.doi.org/10.1371/journal.pcbi.1004989
