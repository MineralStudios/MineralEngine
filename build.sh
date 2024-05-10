mvn clean install
native-image -jar target/MineralEngine-1.0-SNAPSHOT.jar target/MineralEngine --enable-url-protocols=https --gc=G1 -march=native
