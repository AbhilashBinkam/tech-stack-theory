REDIS Keys:

Key names are unique
Key names are Binary safe, which means any binary sequence can be used as a key. (Ex: "Foo",42.231234, 0xff). Long keys are generally not recommended
Key names can be upto 512MB in size and can be increased in future versions of redis

Key Spaces:

A single flat keyspace exists in logical database which means all the key names occupy the same space.
No Automaitc separation of keys names into named groups such as buckets or collections.

Logical Database:

A logical-database is identified by a zero-based index. The default is Database 0.
within a logical database the keynames are unique, but the same key names can appear in multiple logical databases. Hence logical databases do provide separation of key names. In practical terms, logical databases are best suited when you need seperate key spaces for a single application rathern than separating multiple applications.


Structured Key Name: development team can choose the structure of the key names.

--> user:id:followers
	--> user: Object Name
	--> 1000: unique identifier or the instance
	--> followers: composed objects

--> registeredusers:1000:followers
	RegisteredUsers:1000:followers
	registeredUsers:1000:followers

For the above identical keys the server will do a binary comparison on the key name to determine if the key exists before it's retrieved or modified.

REDIS COMMANDS:

> SET key value [EX seconds] [PX milliseconds] [NX|XX
> GET key

Example:

> redis-enterprise:6379> set customer:1000 fred
OK
> redis-enterprise:6379> get customer:1000
"fred"
> redis-enterprise:6379> keys customer:1*
1) "customer:1000"


Two commands for getting a list of existing key names in our Redis database.

> KEYS  --> Blocks until complete
		--> Never used in production
		--> Userful for debugging

> SCAN  --> Iteretes using a cursor
		--> Returns a slot reference
		--> May return 0 or more keys per call


SCAN slot [MATCH pattern] [COUNT count]

To Execut the SCAN command we start by giving the slot position as "0"

> redis-enterprise:6379> scan 0 MATCH customer:1*
1) "14336"
2) (empty list or set)

> redis-enterprise:6379> scan 14336 MATCH customer:1*
1) "14848"
2) (empty list or set)

> redis-enterprise:6379> scan 14336 MATCH customer:1* COUNT 1000
1) "1229"
2) 1) "customer:1500"

> redis-enterprise:6379> scan 1229 MATCH customer:1* COUNT 1000
1) "0"
2) (empty list or set)

scan may take many calls, but ultimately we get the same results we got when running the KEYS command. To force SCAN to look at more keys per call, we can pass COUNT


Remove keys:

> DEL key [key ...] --> It removes the key and the memory associated with the key. This is a blocking operation.
> UNLINK key [key ...] --> Non blocking operation and the memory associated with the key value is reclaimed by an async process

> redis-enterprise:6379> unlink customer:1000
(integer) 1  --> 1 indicates the number of keys got removed
> redis-enterprise:6379> get customer:1000
(nil)

EXISTS keys:

EXISTS Key [key ...]

> redis-enterprise:6379> exists inventory:100-meters-womens-final
(integer) 0 --> returns 0 if no key found and returns 1 if key exists

IMPORTANT: Having a two operations - the EXISTS followed by a SET -- means two round trips REDIS and possible inconsistencies. Another connection may have set a value or removed the key in between those commands.

SET key value [EX seconds] [PX milliseconds] [NX|XX] --> NX to make sure the key doesn't exists before we set it
													--> XX indiates that the key must exists before we apply the value 
> redis-enterprise:6379> set inventory:100-meters-womens-final 1000 NX
OK
> redis-enterprise:6379> set inventory:100-meters-womens-final "Sold Out" NX
(nil) --> Since the key already exists then the value is left unchanged upon 1000  
> redis-enterprise:6379> set inventory:100-meters-womens-final 0 XX
OK
> redis-enterprise:6379> get inventory:100-meters-womens-final

EXPIRATION OF KEYS:

--> we can define an expiration time or TTL. REDIS will keep the key in memory until space is required or is forced out by the eviction policy in force.
--> Expiration time can be set in milliseconds, seconds or UNIX timestamp. TTL can be set when the key is first created or can be set afterwards.
--> Expiration time for a key can be removed

TTL COMMANDS

SET:
> EXPIRE key seconds
> EXPIREAT key timestamp
> PEXPIRE key milliseconds
> PEXPIREAT key milliseconds-timestamp

EXAMINE:
> TTL key
> PTTL key

REMOVE:
> PERSIST key

> redis-enterprise:6379> set seat-hold Row:A:Seat:4 PX 50000
OK
> redis-enterprise:6379> set seat-hold Row:A:Seat:4 EX 50
OK

Both the above examples result in setting the same value and the same time to live, although their actual expiration will depend on the clock time when the command was executed. Because the key has not expired, when we get the key the value is returned.

> redis-enterprise:6379> get seat-hold
"Row:A:Seat:4"


If the key already exists or you want to expiration
> redis-enterprise:6379> pexpire seat-hold 1
(integer) 1
> redis-enterprise:6379>get seat-hold
(nil)

Once an expiration is set, it can be examined. 

> redis-enterprise:6379> set seat-hold Row:A:Seat:4 EX 50
OK
> redis-enterprise:6379> ttl seat-hold
(integer) 42 --> amount of time left in seconds

> redis-enterprise:6379> pttl seat-hold
(integer) 42 --> amount of time left in milliseconds

> redis-enterprise:6379> persist seat-hold --> we can remove the TTL on the key to ensure the key is retained
(integer) 1

https://redis.io/commands#

























