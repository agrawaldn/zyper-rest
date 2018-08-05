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
            assertEquals("verify",order.getStatus());
        });

    }

    @Test
    public void saveVerifiedOrderTest1(){
        db.deleteKey("v_order-v2::TEST_ORDERID");
        db.deleteKey("v_order_log-v2::TEST_ORDERID");
        db.deleteKey("order-v2::TEST_ORDERID");

        Map<String,String> orderMap = new HashMap();
        orderMap.put("status","verify");
        orderMap.put("start","1533077490838");
        orderMap.put("end","1533077513334");
        orderMap.put("customer_id","TEST_CUSTOMERID");
        orderMap.put("prod::TEST_PROD","1");
        db.saveData("order-v2::TEST_ORDERID",orderMap, "Map");

        Order order = service.getOrder("TEST_ORDERID","verify",false);
        order.setOrderEvents(new ArrayList<OrderEvent>());
        try {
            service.saveVerifiedOrder(order);
            Map<String, String> map = (Map<String, String>) db.fetchData("order-v2::TEST_ORDERID", "Map");
            assertEquals("verify",map.get("status"));
        }catch(Exception ex){
            logger.error(ex.getMessage(),ex);
        }
        db.deleteKey("v_order-v2::TEST_ORDERID");
        db.deleteKey("v_order_log-v2::TEST_ORDERID");
        db.deleteKey("order-v2::TEST_ORDERID");
    }

    @Test
    public void saveVerifiedOrderTest2(){
        db.deleteKey("v_order-v2::TEST_ORDERID");
        db.deleteKey("v_order_log-v2::TEST_ORDERID");
        db.deleteKey("order-v2::TEST_ORDERID");

        Map<String,String> orderMap = new HashMap();
        orderMap.put("status","verify");
        orderMap.put("start","1533077490838");
        orderMap.put("end","1533077513334");
        orderMap.put("customer_id","TEST_CUSTOMERID");
        orderMap.put("prod::TEST_PROD","1");
        db.saveData("order-v2::TEST_ORDERID",orderMap, "Map");

        Order order = service.getOrder("TEST_ORDERID","verify",false);
        OrderEvent e = new OrderEvent();

        List<OrderEvent> eventList = new ArrayList<OrderEvent>();
        eventList.add(e);
        order.setOrderEvents(eventList);
        try {
            service.saveVerifiedOrder(order);
            Map<String, String> map = (Map<String, String>) db.fetchData("order-v2::TEST_ORDERID", "Map");
            assertEquals("verified",map.get("status"));
        }catch(Exception ex){
            logger.error(ex.getMessage(),ex);
        }
        db.deleteKey("v_order-v2::TEST_ORDERID");
        db.deleteKey("v_order_log-v2::TEST_ORDERID");
        db.deleteKey("order-v2::TEST_ORDERID");
    }

    @Test
    public void saveVerifiedOrderTest3(){
        db.deleteKey("v_order-v2::TEST_ORDERID");
        db.deleteKey("v_order_log-v2::TEST_ORDERID");
        db.deleteKey("order-v2::TEST_ORDERID");

        Map<String,String> orderMap = new HashMap();
        orderMap.put("status","verify");
        orderMap.put("start","1533077490838");
        orderMap.put("end","1533077513334");
        orderMap.put("customer_id","TEST_CUSTOMERID");
        orderMap.put("prod::TEST_PROD","1");
        db.saveData("order-v2::TEST_ORDERID",orderMap, "Map");

        Order order = service.getOrder("TEST_ORDERID","verify",false);
        List<OrderEvent> eventList = new ArrayList<OrderEvent>();
        OrderEvent e1 = new OrderEvent();
        e1.setMovements("lhi");
        e1.setLproductAdded("PEPSI");
        e1.setLproductQuantity(3);
        e1.setLshelf("1006");
        e1.setCamera("1234567890");
        e1.setTimestamp("2018-08-01T04:23:48.294+0530");
        e1.setOrigTS("1533077628294");
        eventList.add(e1);
        OrderEvent e2 = new OrderEvent();
        e2.setMovements("rho");
        e2.setRproductAdded("COKE");
        e2.setRproductQuantity(1);
        e2.setRshelf("1048");
        e2.setCamera("1234567890");
        e2.setTimestamp("2018-08-01T04:23:51.072+0530");
        e2.setOrigTS("1533077631072");
        eventList.add(e2);

        order.setOrderEvents(eventList);
        service.saveVerifiedOrder(order);
        Map<String, String> map = (Map<String, String>) db.fetchData("order-v2::TEST_ORDERID", "Map");
        assertNotNull(map);
        assertEquals("verified",map.get("status"));
        map = (Map<String, String>) db.fetchData("v_order-v2::TEST_ORDERID", "Map");
        assertNotNull(map);
        assertEquals("1",map.get("prod::COKE"));
        assertEquals("3",map.get("prod::PEPSI"));
        List<String> lst = (List<String>) db.fetchData("v_order_log-v2::TEST_ORDERID", "List");
        assertNotNull(lst);
        assertEquals(2,lst.size());
        assertEquals("1533077631072:1234567890:-1:-1:-1:-1:-1:-1:-1:-1:(rho):" +
                "sku_id0:coords(-1,-1,-1,-1):shelf(1048)",lst.get(0));
        assertEquals("1533077628294:1234567890:-1:-1:-1:-1:-1:-1:-1:-1:(lhi):" +
                "sku_id0:coords(-1,-1,-1,-1):shelf(1006):" +
                "sku_id1:coords(-1,-1,-1,-1):shelf(1006):" +
                "sku_id2:coords(-1,-1,-1,-1):shelf(1006)",lst.get(1));
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
