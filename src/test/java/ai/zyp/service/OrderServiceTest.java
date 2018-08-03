package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.controller.Application;
import ai.zyp.domain.Order;
import ai.zyp.domain.OrderEvent;
import ai.zyp.domain.OrderItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    OrderService service = new OrderService();
    Redis db = new Redis();

    @Test
    public void getVerifyOrdersTest() {

        List<Order> orders = service.getVerifyOrders();
        assertNotNull(orders);
        orders.forEach(order -> {
            assertNotNull(order);
            assertNotNull(order.getOrderId());
            assertNotNull(order.getCustomerId());
            assertNotNull(order.getStatus());
            assertNotNull(order.getStartTime());
            assertNotNull(order.getEndTime());
            assertEquals(order.getStatus(),"verify");
        });

    }

    @Test
    public void saveVerifiedOrderTest1(){
        Map<String,String> orderMap = new HashMap();
        orderMap.put("status","verify");
        orderMap.put("start","1533077490838");
        orderMap.put("end","1533077513334");
        orderMap.put("customer_id","TEST_CUSTOMERID");
        orderMap.put("prod::TEST_PROD","1");
        db.saveData("order-v2::TEST_ORDERID",orderMap, "Map");

        Order order = service.getOrder("order-v2::TEST_ORDERID","verify",false);
        order.setOrderEvents(new ArrayList<OrderEvent>());
        try {
            service.saveVerifiedOrder(order);
            Map<String, String> map = (Map<String, String>) db.fetchData("order-v2::TEST_ORDERID", "Map");
            assertEquals(map.get("status"), "verified");
        }catch(Exception ex){
            db.deleteKey("order-v2::TEST_ORDERID");
            logger.error(ex.getMessage(),ex);
        }
    }

    @Test
    public void saveVerifiedOrderTest2(){
        Map<String,String> orderMap = new HashMap();
        orderMap.put("status","verify");
        orderMap.put("start","1533077490838");
        orderMap.put("end","1533077513334");
        orderMap.put("customer_id","TEST_CUSTOMERID");
        orderMap.put("prod::TEST_PROD","1");
        db.saveData("order-v2::TEST_ORDERID",orderMap, "Map");

        Order order = service.getOrder("order-v2::TEST_ORDERID","verify",false);
        OrderEvent e = new OrderEvent();
        List<OrderEvent> eventList = new ArrayList<OrderEvent>();
        eventList.add(e);
        order.setOrderEvents(eventList);
        try {
            service.saveVerifiedOrder(order);
            Map<String, String> map = (Map<String, String>) db.fetchData("order-v2::TEST_ORDERID", "Map");
            assertEquals(map.get("status"), "verified");
        }catch(Exception ex){
            db.deleteKey("order-v2::TEST_ORDERID");
            logger.error(ex.getMessage(),ex);
        }
    }

    @Test
    public void orderListSortingTest(){
        List<Order> list = service.getVerifyOrders();
        Long prevOrderTime = null;
        Long currOrderTime = null;
        Iterator<Order> itr = list.iterator();
        int i = 0;
        while(itr.hasNext()){
            if(i==0) {
                prevOrderTime = Long.valueOf(itr.next().getStartTime());
            }else{
                currOrderTime = Long.valueOf(itr.next().getStartTime());
                assertTrue(prevOrderTime>currOrderTime);
                prevOrderTime = currOrderTime;
            }
            i++;
        }
    }
}
