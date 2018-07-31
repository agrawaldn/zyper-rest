## Build instructions for tomcat
#Prerequisites: 
1) java 1.8 installed 
2) git client is installed on build machine 
3) maven is installed on build machine
4) have running instance of REDIS database

Run `git clone https://github.com/agrawaldn/zyper-rest.git`
Run `cd zyper-rest`
Run `mvn clean package` This will generate deployable war file `zyper-rest.war` under target directory.

Place this war file under webapps directory of tomcat (`/opt/tomcat/webapps`). Or you can also deploy it through manager console in tomcat.
Start tomcat instance `sudo service tomcat start` if not already running.

Following environment specific configurations are required to run the application smoothly
1) `application.properties` This file contains database connection details. It needs to be included in classpath. You can either update the properties file packaged inside war file or exploded war file inside webapp directory  or can override properties file loaction using CATALINA_OPTS environment variable.
`export CATALINA_OPTS="-Dproperty-override=/full/path/to/application.properties`
2) `logback.xml` this is the configuration file used for logging.


## For development environment
you can either directly execute main method of Application.class or execute war file using `java -jar target/zyper-rest.war` command.

## About
This is the back-end code for the zyper application containing business logic and database connectivity code. It exposes these services through REST APIs.

