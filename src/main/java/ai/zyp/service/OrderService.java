package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Dev Agrawal on 7/14/18.
 */

public class OrderService {

    private Redis db;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderService(){
        db = new Redis("localhost", Integer.parseInt("0"));
    }

    public List<Order> getVerifyOrders() {
        List<Order> orders = new ArrayList();

        List<String> keys = db.getMatchingKeys("order-v2::"+"*",10000);
        logger.debug("Number of orders = "+keys.size());
        keys.forEach(key ->{
            orders.add(getOrder(key.replaceFirst("order-v2::","")));
        });
        return orders;
    }

    public Order getOrder(String orderId){
        Order order = null;
        logger.debug("getOrder() called with orderId = "+orderId);
        Map<String,String> orderMap = (Map<String,String>)db.fetchData("order-v2::"+orderId,"Map");
        if(orderMap !=null){
            if(orderMap.get("status").equals("verify")){
                order = new Order(orderId,orderMap);
            }
        }else{
            logger.error("No order details fetched from DB for orderId "+orderId);
        }
        return order;
    }

    public void updateOrder(Order order){
        Map<String,String> orderItemMap = new HashMap();
        order.getOrderItems().forEach(item->{
            for(int i=0;i<item.getQuantity();i++){
                orderItemMap.put("prod::"+item.getProductDesc(),item.getProductId());
                orderItemMap.put("customer_id",order.getCustomerId());
            }
        });
        db.saveData("order-v2::"+order.getOrderId(),orderItemMap,"Map");
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();
        List<Order> orders = service.getVerifyOrders();
        orders.forEach(order-> System.out.println(order.getCustomerId()));
    }
}
