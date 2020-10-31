# Tech-stack

1. How to achieve Persistance in Redis Cluster? 

1.a. redis_cluster.conf
> appendonly yes - AOF(Append Only File) is written everytime a user sends a command to REDIS Cluster
> appendfsync no - fulshes the AOF file to disk 
	always -- for every single write to cluster the file is flushed to disk immediately
	every second -- once a second the file is flushed to disk
	no -- which allows the OS to use the default flushing option (30 sec)

use the above 2 commands combination to persist the data in REDIS.

2.a. RDB file - snapshot i.e very consice and very efficiently stored and is useful for backup purposes.
NOTE: when we do RDB save, the redis instance(node) must fork and this can have performance degradation for client. Hence we do RBD saves only on replicas and with CRON expressions. 
we don't interrupt the master which is taking client requests.

2. REDIS Cluster for Sessions
Challenge: Latency (How to debug in REDIS)

Latency Doctor

> latency doctor
Latency monitoring is disabled in this Redis instance. 
You may use "CONFIG SET latency-monitor-threshold <milliseconds>." in order to enable it. 
If we weren't in a deep space mission I'd suggest to take a look at http://redis.io/topics/latency-monitor.

Intrinsic Latency Checker

> redis-cli --intrinsic-latency 100
redis cli has intrinsic latency cheker built-in.
when we run the above command it gives us the list of details that shows us how much intrinsic latency in our env (KERNEL)

Client-Side Latency Checker

> redis-cli --latency -h [hostname_redacted] -p 6328
Defines network latency stats

Slow Query Log
> CONFIG SET slowlog-log-slower-than 10000
set the config to get the queries that are taking more than 10ms.
> CONFIG GET slowlog-log-slower-than


PSYNC and CLUSTER SLOTS
Slow Query Log
> SLOWLOG GET
get the list of queries that are taking more than 10ms which is configured in the above step.
above query displayed the below results

> SLOWLOG GET
	3) (integer) 15596 (15.6ms)
	4) 1) "PSYNC"
	   2) "?"
	   3) "-1"
...
	3) (integer) 38833 (38.8ms)
	4) 1) "CLUSTER"
	   2) "SLOTS"
	   
what is PSYNC? it is an internal command that is used when REDIS replias are subscribing to a master. but the PSYNC operation doesn't happen that often.

what is CLUSTER SLOTS?
when a web Appication is making a requst to redis cluster, firstly the client has to understand what the configuration of the cluster is, since cluster config is dynamic and maintained by the cluster itself.
T0 Seed Nodes: A, B --> client will ask the cluster for cluster slots
T1 Slots: {A:0-5500, B: 5501-11000} --> Cluster responds with each nodes having the slots range (refer 3rd Image)
T2 we can fetch the key that we wanted to fetch 
	--> Lets say the HASH_SLOT = CRC16("mykey") mod 16384
	     HASH_SLOT = 14687
From the hashslot we can see that the range falls in C category (11001-16383) refer Image 3

![alt text] (https://github.com/AbhilashBinkam/Tech-stack/blob/main/blob/REDIS-cluster-slots-1.PNG?raw=true)
![alt text] (https://github.com/AbhilashBinkam/Tech-stack/blob/main/blob/REDIS-cluster-slots-2.PNG?raw=true)
![alt text] (https://github.com/AbhilashBinkam/Tech-stack/blob/main/blob/REDIS-cluster-slots-3.PNG?raw=true)

	   
	   
