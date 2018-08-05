package ai.zyp.DAO;

import ai.zyp.controller.Application;
import ai.zyp.domain.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisTest {

    private Redis db = new Redis();

    @Test
    public void zrangeByScoreTest(){
        Redis redis = new Redis();
        String key ="frame::819112072121";
        Long min = 1531352509986L;
        Long max = 1531352510476L;
        Set<String> set = redis.zrangeByScore(key,min,max,1000);
        set.forEach(s->{
            assertTrue(Long.valueOf(s.replaceFirst(":","")).compareTo(min)>=0);
            assertTrue(Long.valueOf(s.replaceFirst(":","")).compareTo(max)<0);
        });
    }


}
