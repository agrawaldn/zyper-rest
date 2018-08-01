package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.controller.Application;
import ai.zyp.domain.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrderServiceTest {

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


}
