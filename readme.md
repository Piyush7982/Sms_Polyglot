 ## Initialized docker-compose file
~~~ 
    $ docker-compose up -d
~~~

 ##Initialized spring tempelate using spring cli
 ~~~ 
   $ spring init --artifact-id=sms-sender --java-version=25 --group-id=org.meesho -
    -build=gradle --type=gradle-project --dependencies=web,data-redis,kafka,lombok javaSmsSender
 ~~~