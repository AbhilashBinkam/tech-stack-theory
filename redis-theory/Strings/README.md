REDIS Strings are binary safe sequences of bytes.

REDIS String:

--> Store and retrieve a String
--> use case: Storing a JSON object as a String and how to set an expiration time untill its deletion
--> Implications of storing JSON as a String
--> Increment and Decrement an integer value within a String

> redis-enterprise:6379> SET user:101:time-zone UTC-8
OK
> redis-enterprise:6379> GET user:101:time-zone
"UTC-8"

Imagine, we want to provide users with the site usage data. Normally, we send the requst to DataWareHouse, which might take several seconds to complete. BY using STRING we can cache the JSON Response after initital fetch as these responses don't change often.

> redis-enterprise:6379> SET usage:63 '{"balance":699.99, "currency":"USD","lastLogin":1281291212,"maxUsers":10}' EX 7200

OK (7200 seconds == 2 hrs)

By using REDIS as a cache, the subsequent responses that might have taken several seconds to fetch from the datawarehouse will instead be served instantly. 
can check the remaining TTL for a key using TTL command.

> redis-enterprise:6379> TTL usage:63

(integer) 7052

REDIS String in regards to integer manipulation:

--> INCR and INCRBY (we can increment by one value or a specified value/ can also use -ve number to decrement the value). If the commands run on a key that doesn't exists then the REDIS will increment the value appropriately.

Let's run the INCR command with the key that doesn't exists yet.

> redis-enterprise:6379> EXISTS user:23:visit-count

(integer) 0

> redis-enterprise:6379> INCR user:23:visit-count

(integer) 1

--> DECRBY

> redis-enterprise:6379> set inventory:4x100m-womens-final 1000

OK

> redis-enterprise:6379> get inventory:4x100m-womens-final

"1000"

> redis-enterprise:6379> decrby inventory:4x100m-womens-final 1

(integer) 999

we can use TYPE command to get the datatype of a key

> redis-enterprise:6379> type inventory:4x100m-womens-final

string

> redis-enterprise:6379> object encoding inventory:4x100m-womens-final

"int"

How does REDIS decrement the string value stored?

First the server looked at the datatype of the key and it's a string datatype, so the DECRBY command is valid.
Second, the server looked at the encoding of the value and it contained an integer value.
Since the numeric operation was performed on a numeric value, a numeric value is threfore returned.

The contents of the String can be changed between text, number or binary at any point. To Redis it's always string datatype. REDIS supports polymorphism that is the ability to represent different types of data overtime for the same key. with the below commands we are updating the value from 999 to "Sold Out" and decrby on the key gets an error.

> redis-enterprise:6379> set inventory:4x100m-womens-final "Sold Out"

OK

> redis-enterprise:6379> object encoding inventory:4x100m-womens-final

"embstr" --> (represnets text value)

> redis-enterprise:6379> decrby inventory:4x100m-womens-final

(error) ERR value is not an integer orr out of range 
