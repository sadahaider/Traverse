# Traverse
Online open source music sharing service



#### How to run server on localhost:8080

###### Requires 
* Apache Maven 3.3.9
* Docker 1.12.6
* JDK 1.8

###### Notes: 
* Make sure your java compiler is set to 1.8
    * update-alternatives --config javac



```
mvn install
docker build -f Dockerfile -t traverse .
docker run -p 8080:8080 traverse
```