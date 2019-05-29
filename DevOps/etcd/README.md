# etcd

etcd是一个开源的、分布式的键值对数据存储系统，提供共享配置、服务的注册和发现。etcd与zookeeper相比算是轻量级系统，两者的一致性协议也一样，etcd的raft比zookeeper的paxos简单。官方说明如下：

```A highly-available key value store for shared configuration and service discovery.```

etcd作为一个受到ZooKeeper与doozer启发而催生的项目，除了拥有与之类似的功能外，更专注于以下四点。

- 简单：基于HTTP+JSON的API让你用curl就可以轻松使用。
- 安全：可选SSL客户认证机制。
- 快速：每个实例每秒支持一千次写操作。
- 可信：使用Raft算法充分实现了分布式。

分布式系统中的数据分为控制数据和应用数据。etcd的使用场景默认处理的数据都是控制数据，对于应用数据，只推荐数据量很小，但是更新访问频繁的情况。

应用场景有如下几类：

- 场景一：服务发现（Service Discovery）
- 场景二：消息发布与订阅
- 场景三：负载均衡
- 场景四：分布式通知与协调
- 场景五：分布式锁、分布式队列
- 场景六：集群监控与Leader竞选