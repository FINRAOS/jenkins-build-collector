Build Collector Plugin Jenkins
============================

Persists build information.

Information reported includes:
* Name of the build.
* Start time of the build.
* Duration of the build.
* Result of the build: success, failed, aborted etc.
* System properties.
* Environment variables.

Recorders
---------
### Coming Soon...

This plugin has the ability to save additional information about your build that is gathered by other plugins. Examples include subversion and git changelog information, Junit and Cobertura 
test results etc. These extensions can be developed and installed as a separate plugin. The core library for this plugin should discover them at runtime and persist the information.

Persistors
----------
### Elasticsearch 
* `URL` - HTTP URL to an Elasticsearch instance, example: `https://cluster01/`.
* `Index name` - Name of the index to use, example: `jenkins`.
* `Type name` - Name of the type, example: `build`.
* `Pattern` - Regex expression that matches the builds you want to persist information for: `(?i).*(CIBUILD).*`.
* `Connection Timeout` - Time to wait to establish a connection to the Elasticsearch server. 
* `Read Timeout` - Time to wait to for a response from the Elasticsearch server. This is used by the plugin to determine if the insert to the service was successful.
* `Username` - Username for authentication to your elasticsearch instance.
* `Password` - Password for authentication to your elasticsearch instance.

### MongoDB
* `Hostname` - Host on which your MongoDB instance is running., example: `localhost`.
* `Port` - Port on which your MongoDB instance is running., example: `27017`.
* `Collection Name` - Name of your collection in MongoDB., example: `build`.
* `Pattern` - Regex expression that matches the builds you want to persist information for: `(?i).*(CIBUILD).*`.
* `Timeout` - Time to wait to for a response from Mongo. 
* `Username` - Username for authentication to your elasticsearch instance.
* `Password` - Password for authentication to your elasticsearch instance.

### Other Extensions
This plugin can be extended to accomodate more peristors. See our Mongo and Elasticsearch extension in our repository. New extensions can be installed as separate plugin
and the core library should discover them at runtime.


Building
--------

### Setup

In order to build the plugin Maven must be able to pull the necessary dependencies from Jenkins repository.
[settings.xml](docs/settings.xml) contains the necessary Maven settings.

### Build

Run `mvn clean install`.

Installation
------------

Launch Jenkins and go to *Manage Jenkins*, *Manage Plugins*, *Advanced*. Use *Upload Plugin* to upload the hpi file.