# Log Client Queue Entries
## Description

This project provides a function that logs the contents of the client queues.

The **LogClientQueueEntriesFunction**:

- gets the CacheServer instance
- gets the AcceptorImpl instance from the CacheServer
- gets the CacheClientNotifier instance from the AcceptorImpl
- gets the collection of CacheClientProxy instances
- for each CacheClientProxy instance:
  - get its HARegionQueue
  - get the HARegion from the HARegionQueue
  - get the queue keys from the HARegion
  - log each queue key and value

In order to ensure that events are in the client queue when the LogClientQueueEntriesFunction is invoked, the example:

- configures the Region with a CacheListener called **MessageDispatcherSlowStartInitializer** which initializes a static boolean called *CacheClientProxy.isSlowStartForTesting*.
- sets the java system property called *slowStartTimeForTesting* to 120000 (seconds).

The only purpose of these actions is to cause the MessageDispatcher to pause before starting to process the queue. This should never done in an actual production system.

## Initialization
Modify the **GEODE** environment variable in the *setenv.sh* script to point to a Geode installation directory.
## Build
Build the Spring Boot Client Application and Geode Server MBean classes using gradle like:

```
./gradlew clean jar bootJar
```
## Run Example
### Start and Configure Locator and Servers
Start and configure the locator and servers using the *startandconfigure.sh* script like:

```
./startandconfigure.sh
```
### Start RegisterInterest Client
Start a client that registers interest in all keys using the *runclient.sh* script like below. This client will stay up waiting for events.

The parameters are:

- operation (register-interest)

```
./runclient.sh register-interest
```
### Load Entries
Load N Trade instances into the Trade region using the *runclient.sh* script like below.

The parameters are:

- operation (load)
- number of entries (e.g. 100)

```
./runclient.sh load 100
```
### Log Client Queue Entries
Log the client queue entries using the *runclient.sh* script like below.

The parameters are:

- operation (log-all-client-queue-entries)

```
./runclient.sh log-all-client-queue-entries
```
### Shutdown Locator and Servers
Execute the *shutdownall.sh* script to shutdown the servers and locator like:

```
./shutdownall.sh
```
### Remove Locator and Server Files
Execute the *cleanupfiles.sh* script to remove the server and locator files like:

```
./cleanupfiles.sh
```
## Example Output
### Start and Configure Locator and Servers
Sample output from the *startandconfigure.sh* script is:

```
./startandconfigure.sh 
1. Executing - start locator --name=locator --classpath=../server/build/libs/server-0.0.1-SNAPSHOT.jar

.................
Locator in <working-dir>/locator on xxx.xxx.x.xx[10334] as locator is currently online.
Process ID: 6106
Uptime: 24 seconds
Geode Version: 1.15.0-build.0
Java Version: 1.8.0_151
Log File: <working-dir>/locator/locator.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

Successfully connected to: JMX Manager [host=xxx.xxx.x.xx, port=1099]

Cluster configuration service is up and running.

2. Executing - set variable --name=APP_RESULT_VIEWER --value=any

Value for variable APP_RESULT_VIEWER is now: any.

3. Executing - configure pdx --read-serialized=true --auto-serializable-classes=.*

read-serialized = true
ignore-unread-fields = false
persistent = false
PDX Serializer = org.apache.geode.pdx.ReflectionBasedAutoSerializer
Non Portable Classes = [.*]
Cluster configuration for group 'cluster' is updated.

4. Executing - start server --name=server-1 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false --J=-DslowStartTimeForTesting=120000

....
Server in <working-dir>/server-1 on xxx.xxx.x.xx[50753] as server-1 is currently online.
Process ID: 6117
Uptime: 4 seconds
Geode Version: 1.15.0-build.0
Java Version: 1.8.0_151
Log File: <working-dir>/server-1/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

5. Executing - start server --name=server-2 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false --J=-DslowStartTimeForTesting=120000

.....
Server in <working-dir>/server-2 on xxx.xxx.x.xx[50781] as server-2 is currently online.
Process ID: 6118
Uptime: 6 seconds
Geode Version: 1.15.0-build.0
Java Version: 1.8.0_151
Log File: <working-dir>/server-2/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

6. Executing - deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar

 Member  |            JAR            | JAR Location
-------- | ------------------------- | ---------------------------------------------------
server-1 | server-0.0.1-SNAPSHOT.jar | <working-dir>/server-1/server-0.0.1-SNAPSHOT.v1.jar
server-2 | server-0.0.1-SNAPSHOT.jar | <working-dir>/server-2/server-0.0.1-SNAPSHOT.v1.jar

7. Executing - create region --name=Trade --type=PARTITION_REDUNDANT --cache-listener=example.server.callback.MessageDispatcherSlowStartInitializer

 Member  | Status | Message
-------- | ------ | -------------------------------------
server-1 | OK     | Region "/Trade" created on "server-1"
server-2 | OK     | Region "/Trade" created on "server-2"

Cluster configuration for group 'cluster' is updated.

8. Executing - list members

Member Count : 3

  Name   | Id
-------- | --------------------------------------------------------------
locator  | xxx.xxx.x.xx(locator:6106:locator)<ec><v0>:41000 [Coordinator]
server-1 | xxx.xxx.x.xx(server-1:6117)<v1>:41001
server-2 | xxx.xxx.x.xx(server-2:6118)<v2>:41002

9. Executing - list functions

 Member  | Function
-------- | -----------------------------
server-1 | LogClientQueueEntriesFunction
server-2 | LogClientQueueEntriesFunction

10. Executing - list regions

List of regions
---------------
Trade

************************* Execution Summary ***********************
Script file: startandconfigure.gfsh

Command-1 : start locator --name=locator --classpath=../server/build/libs/server-0.0.1-SNAPSHOT.jar
Status    : PASSED

Command-2 : set variable --name=APP_RESULT_VIEWER --value=any
Status    : PASSED

Command-3 : configure pdx --read-serialized=true --auto-serializable-classes=.*
Status    : PASSED

Command-4 : start server --name=server-1 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false --J=-DslowStartTimeForTesting=120000
Status    : PASSED

Command-5 : start server --name=server-2 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false --J=-DslowStartTimeForTesting=120000
Status    : PASSED

Command-6 : deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar
Status    : PASSED

Command-7 : create region --name=Trade --type=PARTITION_REDUNDANT --cache-listener=example.server.callback.MessageDispatcherSlowStartInitializer
Status    : PASSED

Command-8 : list members
Status    : PASSED

Command-9 : list functions
Status    : PASSED

Command-10 : list regions
Status     : PASSED
```
### Start RegisterInterest Client
Sample output from the *runclient.sh* script is:

```
./runclient.sh register-interest

2022-01-19 12:54:57.544  INFO 6366 --- [           main] example.client.Client                    : Starting Client ...
...
2022-01-19 12:55:02.119  INFO 6366 --- [           main] example.client.Client                    : Started Client in 5.058 seconds (JVM running for 5.672)
2022-01-19 12:55:03.956  INFO 6366 --- [           main] example.client.service.TradeService      : Registered interest in all keys for region=/Trade
```
### Load Entries
Sample output from the *runclient.sh* script is:

```
./runclient.sh load 100

2022-01-19 12:55:27.291  INFO 6382 --- [           main] example.client.Client                    : Starting Client ...

2022-01-19 12:55:38.421  INFO 6382 --- [           main] example.client.Client                    : Started Client in 12.172 seconds (JVM running for 15.295)
2022-01-19 12:55:38.426  INFO 6382 --- [           main] example.client.service.TradeService      : Putting 100 trades of size 16 bytes
2022-01-19 12:55:39.296  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=0, cusip=AXP, shares=45, price=112.12, createTime=1642632938427, updateTime=1642632938427)
2022-01-19 12:55:39.331  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=1, cusip=AXP, shares=61, price=201.99, createTime=1642632939296, updateTime=1642632939296)
2022-01-19 12:55:39.345  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=2, cusip=WFC, shares=4, price=219.24, createTime=1642632939331, updateTime=1642632939331)
2022-01-19 12:55:39.364  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=3, cusip=CRM, shares=83, price=90.21, createTime=1642632939346, updateTime=1642632939346)
2022-01-19 12:55:39.373  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=4, cusip=MA, shares=86, price=48.80, createTime=1642632939364, updateTime=1642632939364)
...
2022-01-19 12:55:39.979  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=95, cusip=CMCSA, shares=41, price=706.31, createTime=1642632939973, updateTime=1642632939973)
2022-01-19 12:55:39.983  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=96, cusip=JNJ, shares=29, price=753.47, createTime=1642632939979, updateTime=1642632939979)
2022-01-19 12:55:39.987  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=97, cusip=ORCL, shares=73, price=469.64, createTime=1642632939983, updateTime=1642632939983)
2022-01-19 12:55:39.991  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=98, cusip=MMM, shares=74, price=629.66, createTime=1642632939987, updateTime=1642632939987)
2022-01-19 12:55:39.995  INFO 6382 --- [           main] example.client.service.TradeService      : Saved Trade(id=99, cusip=AXP, shares=27, price=663.35, createTime=1642632939991, updateTime=1642632939991)
```
### Log Client Queue Entries
Sample output from the *runclient.sh* script is:

```
./runclient.sh log-all-client-queue-entries

2022-01-19 12:55:51.821  INFO 6403 --- [           main] example.client.Client                    : Starting Client ...
...
2022-01-19 12:55:54.834  INFO 6403 --- [           main] example.client.Client                    : Started Client in 3.376 seconds (JVM running for 3.821)
2022-01-19 12:55:54.886  INFO 6403 --- [           main] example.client.service.TradeService      : Logged all client queue entries result={server-1=true, server-2=true}
```
The server log files will contain a message like:

```
[info 2022/01/19 12:55:54.876 HST server-2 <ServerConnection on port 50929 Thread 4> tid=0x5f] The server contains the following 1 client queue:
	CacheClientProxy[identity(192.168.1.12(client:6366:loner):50957:98ed8c74:client,connection=1; port=50965; primary=true; version=GEODE 1.14.0] queueSize=101
		key=1; value=org.apache.geode.internal.cache.tier.sockets.ClientMarkerMessageImpl@5a715129
		key=2; value=HAEventWrapper[region=/Trade; key=0; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=0]; no message]
		key=3; value=HAEventWrapper[region=/Trade; key=1; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=1]; no message]
		key=4; value=HAEventWrapper[region=/Trade; key=2; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=2]; no message]
		key=5; value=HAEventWrapper[region=/Trade; key=3; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=3]; no message]
    ...
		key=97; value=HAEventWrapper[region=/Trade; key=95; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=95]; no message]
		key=98; value=HAEventWrapper[region=/Trade; key=96; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=96]; no message]
		key=99; value=HAEventWrapper[region=/Trade; key=97; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=97]; no message]
		key=100; value=HAEventWrapper[region=/Trade; key=98; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=98]; no message]
		key=101; value=HAEventWrapper[region=/Trade; key=99; refCount=1; putInProgress=0; event=EventID[id=31 bytes;threadID=938533;sequenceID=99]; no message]
```
### Shutdown Locator and Servers
Sample output from the *shutdownall.sh* script is:

```
./shutdownall.sh      

(1) Executing - connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host= localhost, port=1099] ..
Successfully connected to: [host= localhost, port=1099]

You are connected to a cluster of version: 1.15.0-build.0


(2) Executing - shutdown --include-locators=true

Shutdown is triggered
```
