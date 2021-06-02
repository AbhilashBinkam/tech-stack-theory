# Tech-stack

## What is Redis Cluster?

* Horizontally Scalable --> a way to have redis instances to form a cluster. Horizontally scalable means adding nodes to serve the capacity
* Auto Data Sharding --> Redis cluster is able to partition and split data among nodes in an automatic way 
* Fault tolerant --> we loose a node or the server went down but we can still continue operating as not data will be lost. High Availability
* Decentralized cluster management (Gossip Protocol) --> Redis cluster uses gossip protocol amongst nodes to communicate on what the configuration of the cluster is all about. we can also send any command to any cluster node inorder to change the cluster. There is no single node which can acts like an orchestrator in Redis cluster, every node participates. 

## Data Sharding in Redis Cluster

* Every key that we store into Redis Cluster is associated with hashslot. There are 0 ~ 16383 hashslots. Let's assume that these hasslots are divided into 3 parts and allocated to 3 master nodes. Each master nodes have 2 replicas configured as shown in the image. If the master goes down then the replica will take the place of master

![data sharing redis][data-sharing-redis]

## How to achieve Persistance in Redis Cluster?

Below are the two ways to achieve persistence in Redis Cluster

AOF(Append Only File) is written everytime a user sends a command/write command to REDIS Cluster
appendfsync - fulshes the AOF file to disk

1. redis_cluster.conf

> appendonly yes 
> appendfsync no

use the above two commands combination to persist the data in REDIS.
Other available options for appendfsync

> appendfsync always

for every single write to cluster the file is flushed to disk immediately

> appendfsync every second

once a second the file is flushed to disk

> appendfsync no

which allows the OS to use the default flushing option (30 sec)


2. RDB file - snapshot i.e very consice and very efficiently stored and is useful for backup purposes.

NOTE: when we do RDB save, the redis instance(node) must fork and this can have performance degradation for client. Hence we do RBD saves only on replicas and with CRON expressions. 
we don't interrupt the master which is taking client requests.

## Sample real-time implementation of redis-cluster specifications

* 32 masters, 64 slaves
* 25 GB per instance
* 8 instances per host
* 12,000 total RPS

## REDIS is single threaded and we get more value for money if we co-locate instances on a single host

## REDIS Cluster for Sessions
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

> redis-cli --latency -h [hostname_redacted] -p 6382

Defines network latency stats

Slow Query Log -- This can be configured to record the events takes palce in redis which can take longer than certain amount of time. 

> CONFIG SET slowlog-log-slower-than 10000

The above command sets the config to get the queries that are taking more than 10ms.

> CONFIG GET slowlog-log-slower-than


PSYNC and CLUSTER SLOTS

Slow Query Log

> SLOWLOG GET

get the list of queries that are taking more than 10ms which is configured in the above step.
above query displayed the below results

> SLOWLOG GET
---
	3) (integer) 15596 (15.6ms)
	4) 1) "PSYNC"
	   2) "?"
	   3) "-1"
---
	3) (integer) 38833 (38.8ms)
	4) 1) "CLUSTER"
	   2) "SLOTS"
	   
## what is PSYNC? 
PSYNC is an internal command that is used when REDIS replias are subscribing to a master. but the PSYNC operation doesn't happen that often.


## what is CLUSTER SLOTS?
when a web Appication is making a requst to redis cluster, firstly the client has to understand what the configuration of the cluster is, since cluster config is dynamic 
and maintained by the cluster itself.

* At time T0 --> client will ask the cluster for cluster slots --> Seed Nodes: A, B and C
* At time T1 --> Before we make a request to the cluster the client has to update it's understanding on what the cluster config is, since cluster config is dynamic and maintained by the cluster so we've to ask the cluster for cluster slots --> In response we get the slots range  --> Slots: {A:0-5500, B:5501-11000} --> Cluster responds with each nodes having the slots range
* At time T2 --> we can fetch the key that we wanted to fetch --> Lets say the HASH_SLOT = CRC16("mykey") mod 16384 HASH_SLOT = 14687

From the hashslot we can see that the range falls in C category (11001-16383)

![Cluster slot One][cluster-slot-1]
![Cluster slot two][cluster-slot-2]
![Cluster slot three][cluster-slot-3]


For every user request from client, the cluster slots get triggered first and then followed by the retireval of the key that we wanted. This process can happen thousands of times per second. 
The latency is request processing can occur when the client is not able to user the cluster slots config that was previously fetched.

## Problem Statement:

Ideally at the beginning of the start up we've to do one cluster slots for request and then remember the result, but the cluster can reconfigure itself at anytime. 
so we need to account for that change, if there is a moved response we've to refresh our cluser slots (in case of node failure and so on).
![Cluster slot challenge one][cluster-slot-1]

## Solution: 

we can store the cluster slot in local APC Cache on the webApplication server. This can reduce the latency
![Cluster slot challenge two][cluster-slot-1]

[cluster-slot-1]: redis-theory/blob/REDIS-cluster-slots-1.PNG
[cluster-slot-2]: redis-theory/blob/REDIS-cluster-slots-2.PNG
[cluster-slot-3]: redis-theory/blob/REDIS-cluster-slots-3.PNG
[Cluster-slot-challenge-one]: redis-theory/blob/REDIS-cluster-slots-challenge-solution.PNG
[Cluster-slot-challenge-two]: redis-theory/blob/REDIS-cluster-slots-challenge.PNG
[data-sharing-redis]: redis-theory/blob/data-sharding-redis.PNG