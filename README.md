Libre Metrics Service Client
============================

## About

The Libre Metrics Service Client is a Java Client library that can be used to
abstract the calling of the [Libre Metrics Service](https://github.com/nsidc/libre-metrics-service) directly, making it easier to incorporate metrics reporting in other Java projects 

This project is being released as partial fullfillment of an NSF award number
ARC 0946625.  This is non-working software and has not been previously
distributed.  All of the code references to internal networks and servers have
been removed throughout the project.  Any potential user will have to modify
the source for their environemnt in order to use this code.  License
information is provided in the LICENSE.txt file. 

## Compilation notes:

This package was created using maven 2.2.0 which has some compatibility issues
with the latest maven release. In case you run into these issues try with maven
2.2.0

The `net.sf.javainetlocator` package is not included but it is necessary to build the client. It can be downloaded from: http://javainetlocator.sourceforge.net/

## Credit

This software was developed by the National Snow and Ice Data Center under NSF
award number ARC 0946625 and NASA award number NNX10AE07A.

