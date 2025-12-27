 ## Initialized docker-compose file
~~~ 
    $ docker-compose up -d
~~~

 ## Initialized spring tempelate using spring cli
 ~~~ 
   $ spring init --artifact-id=sms-sender --java-version=25 --group-id=org.meesho -
    -build=gradle --type=gradle-project --dependencies=web,data-redis,kafka,lombok javaSmsSender
 ~~~

 ## Build Springboot using 
 ~~~
  $ cd javaSmsSender
  $ ./gradlew bootRun
 ~~~


 ## Curl Commands for api calling

 ### For sending sms
 ~~~
 $ curl -X POST http://localhost:8080/v1/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"7777777778","message":"Hello from curl"}'
 ~~~
 ### For Blocking a number

 ~~~
 $ curl -X POST "http://localhost:8080/v1/sms/blacklist?phoneNumber=7777777777"
 ~~~

 ### Starting go service

 ~~~
 $ cd goSmsReciever
 $ go run main.go
 ~~~

### Curl command for retrieveing data
~~~
$ http://localhost:8081/v1/user/7777777778/messages
~~~