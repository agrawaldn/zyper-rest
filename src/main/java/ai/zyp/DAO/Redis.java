package ai.zyp.DAO;

import ai.zyp.conf.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
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

    public Redis(){

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
            logger.debug("Connected to database at "+ AppProperties.getInstance().getHost());
        }else{
            logger.error("Can't connect to database server"+ AppProperties.getInstance().getHost());
        }
    }

    private Jedis getConnection(){
        Jedis jedis = new Jedis(AppProperties.getInstance().getHost());
        String idx = AppProperties.getInstance().getIndex();
        if(null!= idx && !idx.isEmpty()) {
            jedis.select(Integer.parseInt(idx));
        }
        return jedis;
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

    public void deleteKey(String key) {
        Jedis jedis = this.getConnection();
        try{
            jedis.del(key);
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

            ret.addAll(scanResult.getResult().stream().collect(Collectors.toList()));
            cur = scanResult.getStringCursor();
        } while (!cur.equals(SCAN_POINTER_START));
        return ret;
    }

//    public List<Map.Entry<String, String>> getMatchingFields(String key, String pattern, int countLimit){
//        Jedis jedis = this.getConnection();
//        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
//        ScanParams scanParams = new ScanParams().count(countLimit).match(pattern);
//        String cur = SCAN_POINTER_START;
//        do {
//            ScanResult<Map.Entry<String,String>> scanResult =
//                    jedis.hscan(key, cur, scanParams);
//
//            list.addAll(scanResult.getResult());
//            cur = scanResult.getStringCursor();
//        } while (!cur.equals(SCAN_POINTER_START));
//        return list;
//    }

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

//    public static void main(String[] args) {
//
//        Redis redis = new Redis();
//        List<Map.Entry<String, String>> list = redis.getMatchingFields("order-v2::o-07c26f2e-e4f5-45e5-b936-7a86d1668d11","verify", 1000);
//        list.forEach(entry->{
//            System.out.println("(k,v) =>"+entry.getKey()+","+entry.getValue());
//        });
//    }
}
