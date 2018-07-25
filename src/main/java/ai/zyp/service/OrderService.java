package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.Order;
import ai.zyp.domain.OrderItem;
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
            orders.add(getOrder(key.replaceFirst("order-v2::",""),"verify", false));
        });
        return orders;
    }

    public Order buildOrder(String orderId, Map<String,String> inputMap, boolean includeItems){
        Order order = new Order();
        order.setOrderId(orderId);
        inputMap.forEach((k,v)->{
            switch (k){
                case "customer_id": order.setCustomerId(v);
                    break;
                case "status": order.setStatus(v);
                    break;
                case "start": order.setStartTime(v);
                    break;
                case "end": order.setEndTime(v);
                    break;
                case "Paid": order.setPaid(Double.parseDouble(v));
                    break;
                default:
                    if(includeItems) {
                        if (k.startsWith("prod::")) {
                            OrderItem item = new OrderItem(v, k.replaceFirst("prod::", ""), 1);
                            order.addOrderItem(item);
                        }
                        if (k.startsWith("qty::")) {
                            String prod = k.replaceFirst("qty::", "");
                            int qty = Integer.parseInt(v);
                            order.updateProdQuantity(prod, qty);
                        }
                    }
            }

        });
        return order;
    }
    public Order getOrder(String orderId, String status, boolean includeItems){
        Order order = null;
        logger.debug("getOrder() called with orderId = "+orderId);
        Map<String,String> orderMap = (Map<String,String>)
                db.fetchData("order-v2::"+orderId,"Map");
        if(orderMap !=null){
            if( status != null && !status.isEmpty()) {
                if (orderMap.get("status").equals(status)) {
                    order = buildOrder(orderId, orderMap, includeItems);
                }
            }else{
                order = buildOrder(orderId, orderMap, includeItems);
            }
        }else{
            logger.error("No order details fetched from DB for orderId "+orderId);
        }
        return order;
    }

    private Map<String,String> getMapForSave(Order order, String fieldPrefix){
        Map<String,String> saveMap = new HashMap();
        if(fieldPrefix == null){
            fieldPrefix = "";
        }
        Iterator<OrderItem> iterator =order.getOrderItems().iterator();
        while(iterator.hasNext()){
            OrderItem item = iterator.next();
            saveMap.put(fieldPrefix+"prod::"+item.getProductDesc(),item.getProductId());
            saveMap.put(fieldPrefix+"qty::"+item.getProductDesc(),""+item.getQuantity());
        }
        return saveMap;
    }

    public void saveVerifiedOrder(Order order){
        logger.debug("Received verify order - "+order.toString());
        Map<String,String> saveMap = getMapForSave(order,"verify_");
        saveMap.put("status","verified");
        db.saveData("order-v2::"+order.getOrderId(),saveMap,"Map");
    }

    /**
     * Use this function to replace orderItems with the supplied ones
     * @param order
     */
    public void updateOrderItems(Order order){
        Order oldOrder = this.getOrder(order.getOrderId(),null, true);
        db.deleteDataFromHash("order-v2::"+oldOrder.getOrderId(),
                getMapForSave(oldOrder,null).keySet());
        db.saveData("order-v2::"+order.getOrderId(),
                getMapForSave(order, null),"Map");
    }

    public void updateOrderStatus(String orderId, String status){
        db.updateData("order-v2::"+orderId, "status", status);
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();
        Order order = service.getOrder("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f",null, true);
        System.out.println("Original "+order.toString());
        order.addOrderItem(new OrderItem("567","JUICE",2));
        //order.getOrderItems().remove(0);
        System.out.println("To be updated"+order.toString());
        service.saveVerifiedOrder(order);

        System.out.println("After update"+service.getOrder("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f",
                null, true).toString());
        //service.updateOrderStatus("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f","verify");
    }
}
