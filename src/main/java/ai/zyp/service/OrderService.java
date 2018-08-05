package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.Order;
import ai.zyp.domain.OrderEvent;
import ai.zyp.domain.OrderItem;
import ai.zyp.domain.OrderSortByStartTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * Created by Dev Agrawal on 7/14/18.
 */

public class OrderService {

    private Redis db;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderService() {
        db = new Redis();
    }

    public List<Order> getVerifyOrders() {
        List<Order> orders = new ArrayList();

        List<String> keys = db.getMatchingKeys("order-v2::" + "*", 10000);

        keys.forEach(key -> {
            Order order = getOrder(key.replaceFirst("order-v2::", ""), "verify", false);
            if (null != order){
                orders.add(order);
            }
        });
        Collections.sort(orders,new OrderSortByStartTime());
        logger.debug("Total number of orders in verify status = " + orders.size());
        return orders;
    }

    public Order buildOrder(String orderId, Map<String, String> inputMap, boolean includeItems) {
        Order order = new Order();
        order.setOrderId(orderId);
        inputMap.forEach((k, v) -> {
            switch (k) {
                case "customer_id":
                    order.setCustomerId(v);
                    break;
                case "status":
                    order.setStatus(v);
                    break;
                case "start":
                    order.setStartTime(v);
                    break;
                case "end":
                    order.setEndTime(v);
                    break;
                case "Paid":
                    order.setPaid(Double.parseDouble(v));
                    break;
                default:
                    if (includeItems) {
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

    public Order getOrder(String orderId, String status, boolean includeItems) {
        Order order = null;
        //logger.debug("getOrder() called with orderId = " + orderId);
        Map<String, String> orderMap = (Map<String, String>)
                db.fetchData("order-v2::" + orderId, "Map");
        if (null != orderMap) {
            if (status != null && !status.isEmpty()) {
                if (orderMap.containsKey("status") && orderMap.get("status").equals(status)) {
                    order = buildOrder(orderId, orderMap, includeItems);
                }
            } else {
                order = buildOrder(orderId, orderMap, includeItems);
            }
        } else {
            logger.error("No order details fetched from DB for orderId " + orderId);
        }
        return order;
    }

    private Map<String, String> getMapForSave(Order order, String fieldPrefix) {
        Map<String, String> saveMap = new HashMap();
        if (fieldPrefix == null) {
            fieldPrefix = "";
        }
        Iterator<OrderItem> iterator = order.getOrderItems().iterator();
        while (iterator.hasNext()) {
            OrderItem item = iterator.next();
            saveMap.put(fieldPrefix + "prod::" + item.getProductDesc(), item.getProductId());
            saveMap.put(fieldPrefix + "qty::" + item.getProductDesc(), "" + item.getQuantity());
        }
        return saveMap;
    }

    public void saveVerifiedOrder(Order order) {
        String fieldPrefix = "v_";
        Map<String, String> saveMap = new HashMap();
        List<String> logList = new ArrayList();
        boolean okToVerify = true;
        //do not update status if there are no events recorder at verification stage
        if(null!=order.getOrderEvents() && order.getOrderEvents().size()>0) {
            Iterator<OrderEvent> itr = order.getOrderEvents().iterator();
            while (itr.hasNext()) {
                OrderEvent e = itr.next();
                if (null != e) {
                    generateItemMap(saveMap, e);
                    String log =generateVerifiedLog(e);
                    if(null != log) {
                        logList.add(log);
                    }
                }else{
                    logger.error("Event null for verification of order: "+order.getOrderId());
                    okToVerify = false;
                }
            }
            if(logList.size()==0){
                logger.error("No annotations were recorded for order: "+order.getOrderId());
                okToVerify = false;
            }
            long epochTime = Calendar.getInstance().getTime().getTime();
            saveMap.put("verified", epochTime + "");
            if(okToVerify) {
                Jedis jedis = db.getConnection();
                Transaction tran = jedis.multi();
                tran.hset("order-v2::" + order.getOrderId(),"status","verified");
                tran.hmset(fieldPrefix + "order-v2::" + order.getOrderId(), saveMap);
                logList.forEach(value -> tran.lpush(fieldPrefix + "order_log-v2::" + order.getOrderId(), value));
                tran.exec();
            }
        }
    }

    private String generateVerifiedLog(OrderEvent e) {
        StringBuffer value= new StringBuffer();

        if(null != e.getOrigTS())
            value.append(e.getOrigTS()).append(":");
        if(null!=e.getCamera())
            value.append(e.getCamera()).append(":");
        value.append("-1:-1:-1:-1:-1:-1:-1:-1:(");
        if(null!=e.getMovements())
            value.append(e.getMovements()).append(")");
        if(null!=e.getLproductAdded()) {
            for(int i = 0;i<e.getLproductQuantity();i++) {
                value.append(":sku_id"+i+":coords(-1,-1,-1,-1)");
                value.append(":shelf(").append(e.getLshelf()).append(")");
            }
        }
        if(null!=e.getRproductAdded()) {
            for(int i = 0;i<e.getRproductQuantity();i++) {
                value.append(":sku_id"+i+":coords(-1,-1,-1,-1)");
                value.append(":shelf(").append(e.getRshelf()).append(")");
            }
        }
        if(value.length()<=0)
            return null;
        return value.toString();
    }



    private void generateItemMap(Map<String,String> saveMap, OrderEvent e){
        if(null!= e.getLproductAdded() && !e.getLproductAdded().isEmpty()){
            if(saveMap.containsKey("prod::"+e.getLproductAdded())) {
                int qty = Integer.parseInt(saveMap.remove("prod::"+e.getLproductAdded()));
                int sum = qty+e.getLproductQuantity();
                saveMap.put("prod::"+e.getLproductAdded(),sum+"");
            }else{
                saveMap.put("prod::"+e.getLproductAdded(),""+e.getLproductQuantity());
            }
        }
        if(null!= e.getRproductAdded() && !e.getRproductAdded().isEmpty()){
            if(saveMap.containsKey("prod::"+e.getRproductAdded())) {
                int qty = Integer.parseInt(saveMap.remove("prod::"+e.getRproductAdded()));
                int sum = qty+e.getRproductQuantity();
                saveMap.put("prod::"+e.getRproductAdded(),sum+"");
            }else{
                saveMap.put("prod::"+e.getRproductAdded(),""+e.getRproductQuantity());
            }
        }
        if(null!= e.getLproductRemoved() && !e.getLproductRemoved().isEmpty()){
            if(saveMap.containsKey("prod::"+e.getLproductRemoved())) {
                int qty = Integer.parseInt(saveMap.remove("prod::"+e.getLproductRemoved()));
                int sum = qty-e.getLproductQuantity();
                saveMap.put("prod::"+e.getLproductRemoved(),sum+"");
            }else{
                int sum  = -e.getLproductQuantity();
                saveMap.put("prod::"+e.getLproductRemoved(),sum+"");
            }
        }
        if(null!= e.getRproductRemoved() && !e.getRproductRemoved().isEmpty()){
            if(saveMap.containsKey("prod::"+e.getRproductRemoved())) {
                int qty = Integer.parseInt(saveMap.remove("prod::"+e.getRproductRemoved()));
                int sum = qty-e.getRproductQuantity();
                saveMap.put("prod::"+e.getRproductRemoved(),sum+"");
            }else{
                int sum  = -e.getRproductQuantity();
                saveMap.put("prod::"+e.getRproductRemoved(),sum+"");
            }
        }
    }

//    public void saveVerifiedOrder(Order order){
//        logger.debug("Received verify order - "+order.toString());
//        Map<String,String> saveMap = getMapForSave(order,"verify_");
//        saveMap.put("status","verified");
//        db.saveData("order-v2::"+order.getOrderId(),saveMap,"Map");
//    }

//    /**
//     * Use this function to replace orderItems with the supplied ones
//     * @param order
//     */
//    public void updateOrderItems(Order order){
//        Order oldOrder = this.getOrder(order.getOrderId(),null, true);
//        db.deleteDataFromHash("order-v2::"+oldOrder.getOrderId(),
//                getMapForSave(oldOrder,null).keySet());
//        db.saveData("order-v2::"+order.getOrderId(),
//                getMapForSave(order, null),"Map");
//    }

    public void updateOrderStatus(String orderId, String status){
        db.updateData("order-v2::"+orderId, "status", status);
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();
        Order order = service.getOrder("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f",null, false);
        System.out.println("Original "+order.toString());
        //order.addOrderItem(new OrderItem("567","JUICE",2));
        List<OrderEvent> eventList = new ArrayList();
        OrderEvent e = new OrderEvent();
        e.setCamera("camerawjvhdfiuv309rifewlv");
        e.setTimestamp("532417094170");
        e.setMovements("lhi rho");
        e.setLproductAdded("PEPSI");
        e.setLproductQuantity(1);
        eventList.add(e);
        order.setOrderEvents(eventList);
        //order.getOrderItems().remove(0);
        //System.out.println("To be updated"+order.toString());
        service.saveVerifiedOrder(order);

        //System.out.println("After update"+service.getOrder("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f",
        //        null, true).toString());
        //service.updateOrderStatus("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f","verify");
    }
}
