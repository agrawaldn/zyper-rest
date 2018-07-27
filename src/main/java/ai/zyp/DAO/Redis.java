package ai.zyp.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;
import java.util.stream.Collectors;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

/**
 * Created by Dev Agrawal on 7/17/18.
 */

public class Redis {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //private static JedisPool pool = null;

    public Redis(String host, int port){
        //pool = new JedisPool(host);
        Jedis jedis = this.getConnection();
        String res = "";
        try {
            res = jedis.ping();
        }catch (JedisException e) {
            logger.error(e.getMessage(),e);
            if (null != jedis) {
                jedis.close();
                jedis = null;            }
        } finally {
            if (null != jedis)
                jedis.close();
            jedis = null;
        }
        if(res.equals("PONG")) {
            logger.debug("Redis Server is running");
        }else{
            logger.error("Can't connect to Redis server");
        }
    }

    private Jedis getConnection(){
        //return pool.getResource();
        return new Jedis();
    }
    public void insertData(String key, String field, String value){
        Jedis jedis = this.getConnection();
        try {
            jedis.hset(key, field, value );
        }catch (JedisException e) {
            logger.error(e.getMessage(),e);
            if (null != jedis) {
                jedis.close();
                jedis = null;            }
        } finally {
            if (null != jedis)
                jedis.close();
            jedis = null;
        }
    }

    public void saveData(String key, Object value, String dataType){
        Jedis jedis = this.getConnection();
        try {
            switch (dataType) {
                case "String":
                    jedis.set(key, (String)value);
                break;
                case "List":
                    jedis.lpush(key, (String)value);
                    break;
                case "Set":
                    jedis.sadd(key, (String)value);
                    break;
                case "Map":
                    jedis.hmset(key, (Map<String, String>) value);
                    break;
            }
        }catch (JedisException e) {
            logger.error(e.getMessage(),e);
            if (null != jedis) {
                jedis.close();
                jedis = null;            }
        } finally {
            if (null != jedis)
                jedis.close();
            jedis = null;
        }
    }

    public void deleteDataFromHash(String key, Set<String> fields) {
        Jedis jedis = this.getConnection();
        try {
            Iterator<String> itr = fields.iterator();
            while (itr.hasNext()) {
                jedis.hdel(key, itr.next());
            }
        } catch (JedisException e) {
            logger.error(e.getMessage(), e);
            if (null != jedis) {
                jedis.close();
                jedis = null;            }
        } finally {
            if (null != jedis) {
                jedis.close();
                jedis = null;
            }
        }
    }

    public Object fetchData(String key, String dataType){
        Object ret = null;
        Jedis jedis = this.getConnection();
        try {
            switch (dataType) {
                case "String":
                    ret = (String)jedis.get(key);
                    break;
                case "List":
                    ret = (List<String>)jedis.lrange(key,0,-1);
                    break;
                case "Set":
                    ret = (Set)jedis.smembers(key);
                    break;
                case "Map":
                    ret = (Map<String,String>)jedis.hgetAll(key);
                    break;
            }

        }catch (JedisException e) {
            logger.error(e.getMessage(),e);
            if (null != jedis) {
                jedis.close();
                jedis = null;
            }
        } finally {
            if (null != jedis) {
                jedis.close();
                jedis = null;
            }
        }
        return ret;
    }

    public void updateData(String key, String field, String value){
        Jedis jedis = this.getConnection();
        try {
            jedis.hset(key,field,value);
        }catch (JedisException e) {
            logger.error(e.getMessage(),e);
            if (null != jedis) {
                jedis.close();
                jedis = null;            }
        } finally {
            if (null != jedis) {
                jedis.close();
                jedis = null;
            }
        }
    }

    public void saveList(String key, List<String> values){
            values.forEach(value -> saveData(key, value, "List"));
    }

    public List<String> getMatchingKeys(String pattern, int countLimit){
        Jedis jedis = this.getConnection();
        List<String> ret = new ArrayList<String>();
        ScanParams scanParams = new ScanParams().count(countLimit).match(pattern);
        String cur = SCAN_POINTER_START;
        do {
            ScanResult<String> scanResult = jedis.scan(cur, scanParams);

            // add to return list
            ret.addAll(scanResult.getResult().stream().collect(Collectors.toList()));
            cur = scanResult.getStringCursor();
        } while (!cur.equals(SCAN_POINTER_START));
        return ret;
    }

    public Set<String> zrangeByScore(String key, long min, long max, int limit) {
        Jedis jedis = null;
        boolean success = true;
        Set<String> ret = null;
        try {
            jedis = getConnection();
            if (jedis == null) {
                success = false;
                return ret;
            }
            ret = jedis.zrangeByScore(key, min, max, 0, limit);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            success = false;
        } finally {
            if (null != jedis) {
                jedis.close();
                jedis = null;
            }
        }
        return ret;
    }

    public static void main(String[] args) {

        Redis redis = new Redis("localhost", 0);
        Set<String> set = redis.zrangeByScore("frame::819112072121",1531352509986L,1531352510476L,1000);
        set.forEach(s->{
            System.out.println(s);
        });
    }
}
