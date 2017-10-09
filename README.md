# Traverse
Online open source music sharing service



#### Running server on localhost:8080

###### Requires 
* Apache Maven 3.3.9
* Docker 1.12.6
* JDK 1.8

###### Notes: 
* Make sure your java compiler is set to 1.8
    * update-alternatives --config javac

Create your applications.properties file in src\main\resources

Fill in cloud.aws_access_key_id and cloud.aws_secret_access_key with your AWS IAM key. 
Make sure your IAM key has Administrator Access.
We will be creating and modifying dynamodb and tables which will require those access permissions.

Fill in spring.social.facebook.appId and spring.social.facebook.appSecret with your Facebook developer app key.
You can create an app over on developers.facebook.com

```
spring.mvc.view.suffix=.html

logging.level.org.springframework=TRACE
logging.level.com=TRACE

server.port=8080

cloud.dynamoDB_table_name_users=traverse_users
cloud.dynamoDB_table_name_audio=traverse_audio
cloud.dynamoDB_table_name_auth=traverse_auth
cloud.s3_bucket=traversebucket

spring.http.multipart.max-file-size=20MB

cloud.aws_access_key_id=
cloud.aws_secret_access_key=

spring.social.facebook.appId=
spring.social.facebook.appSecret=

```

After that is finished, you can begin to build and deploy.

```
mvn install
docker build -f Dockerfile -t traverse .
docker run -p 8080:8080 traverse
```

[Live Version](http://traverse.dax.cloud/)