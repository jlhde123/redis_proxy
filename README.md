# redis_proxy
个人实现的redis代理中间件，根据key值进行转发（一致hash）

com.jlh.core.JedisSocket为入口函数

server.properties 可以指定代理服务器的相关配置
#通过指定以下参数指定redis 集群
redis.server.sk1=127.0.0.1:6379
redis.server.sk2=127.0.0.1:6479 

#定期检查redis集群是否可用
redis.clear.time=30
#代理端口
redis.proxy.port=5556
#连接池并发数
redis.proxy.maxPool=18
 
 
com.jlh.core.RedisService 为测试用例
