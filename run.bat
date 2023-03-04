# Start the ZooKeeper service
start /B "" %KAFKA_HOME%\bin\windows\zookeeper-server-start.bat %KAFKA_HOME%\config\zookeeper.properties

# Start the Kafka broker service
start /B "" %KAFKA_HOME%\bin\windows\kafka-server-start.bat %KAFKA_HOME%\config\server.properties

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=kafka --server.port=8081"