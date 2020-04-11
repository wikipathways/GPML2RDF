#!/bin/bash

wpid=$1
match="/${wpid}/,/Tests/p"

WPID=${wpid} mvn clean test -Dtest=org.wikipathways.wp2rdf.COVIDPathwayTest \
  | sed -n ${match} \
  | grep -v ".bridge" \
  | grep -v "=== ${wpid}" \
  | grep -v "Tests run" \
  | grep -v "Running org" \
  > ${wpid}.ttl
