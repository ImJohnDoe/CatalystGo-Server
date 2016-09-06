package catalystgo.com.cg.cat.ucc;

import redis.clients.jedis.Jedis;

public class RedisAccess {
	private Jedis jedis = new Jedis("localhost");
	
	public RedisAccess() {
		
	}
	
	public void add(String key, String data) {
		jedis.set(key, data);
	}
	
	public void get(String key) {
		String value = jedis.get(key);
		System.out.println(value);
	}
}
