# threads_monitoring
Developing a utility for monitoring the state of Java application threads

###How to run this utility

1. clone project
   
    git clone https://github.com/Diploma-works/threads_monitoring.git
   
2. Open project directiory
3. Build project by

    mvn clean compile assembly:single
   
4. Run jar with given args

    java -jar target/debug-tool-1.0-SNAPSHOT-jar-with-dependencies.jar x_properties=properties.xml mode=web
5. Open localhost:8081/debug-tool/index.html in your browser

###How to prepare environment for testing this utility
1. Install apache tomcat on your device
2. Create a file setenv.sh in a directory $CATALINA_HOME/bin/
3. Put this content to a recently created file

   CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9000 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
4. Run tomcat by execution

   sh $CATALINA_HOME/bin/startup.sh
