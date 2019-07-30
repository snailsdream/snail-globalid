# 说明
在分布式系统中都涉及到全局序列号的问题，网上分布式id生成的核心思路大概有以下几种
1. 基于redis的原子操作，java中spring提供了*RedisAtomicLong*可进行*long*型的线程安全操作
1. 基于mysql的自增主键
1. 基于雪花算法*Snowflake*
1. 基于*zookeeper*的全局id，*curator*提供了*DistributedAtomicLong*可进行*long*型的线程安全操作
1. 基于UUID
本人以前做过一个java实现的基于*Snowflake*实现的全局id生成器，当时需求是生成20位的id，主要解决了机器启动时机器id的无法确定的问题，依赖*zookeeper*。[github地址](https://github.com/snailsdream/snowflake-zookeeper),在了解到更多的分布式id生成方案后，对分布式id生成又有了新的思路。
