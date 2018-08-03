package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.Order;
import ai.zyp.domain.OrderEvent;
import ai.zyp.domain.OrderItem;
import ai.zyp.domain.OrderSortByStartTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (orderMap != null) {
            if (status != null && !status.isEmpty()) {
                if (orderMap.get("status").equals(status)) {
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
        String fieldPrefix = "verified_";
        Map<String, String> saveMap = new HashMap();
        Iterator<OrderEvent> itr = order.getOrderEvents().iterator();
        String eventIdString = "";
        while (itr.hasNext()) {
            OrderEvent e = itr.next();
            if(null!= e) {
                generateItemMap(saveMap, e, fieldPrefix);

                String eventId = UUID.randomUUID().toString();
                saveEvent(fieldPrefix + "event-v2::" + eventId, e);
                if (eventIdString.isEmpty()) {
                    eventIdString = eventIdString + eventId;
                } else {
                    eventIdString = eventIdString + "," + eventId;
                }
            }

        }
        saveMap.put(fieldPrefix + "Events", eventIdString);
        saveMap.put("status","verified");
        db.saveData("order-v2::"+order.getOrderId(),saveMap,"Map");
    }

    private void saveEvent(String key, OrderEvent e) {

        Map<String, String> saveMap = new HashMap<String,String>();
        //db.insertData(key,"camera",e.getCamera() );
        if(null != e.getCamera())
            saveMap.put("camera", e.getCamera());
        if(null!=e.getEpochTimestamp())
            saveMap.put("timestamp", e.getEpochTimestamp());
        if(null!=e.getMovements())
            saveMap.put("movements", e.getMovements());
        if(null!=e.getLproductAdded())
            saveMap.put("l_prod_add", e.getLproductAdded());
        if(null!=e.getLproductRemoved())
            saveMap.put("l_prod_remove", e.getLproductRemoved());
        if(e.getLproductQuantity() >0)
            saveMap.put("l_qty", e.getLproductQuantity() + "");
        if(null!=e.getRproductAdded())
            saveMap.put("r_prod_add", e.getRproductAdded());
        if(null!=e.getRproductRemoved())
            saveMap.put("r_prod_remove", e.getRproductRemoved());
        if(e.getRproductQuantity()>0)
            saveMap.put("r_qty", e.getRproductQuantity() + "");
        if(null!=e.getLshelf())
            saveMap.put("l_shelf",e.getLshelf());
        if(null!=e.getRshelf())
            saveMap.put("r_shelf",e.getRshelf());
        //logger.debug("saveEvent() called with key = "+key+" value = "+saveMap.toString());
        db.saveData(key,saveMap,"Map");
    }



    private void generateItemMap(Map<String,String> saveMap, OrderEvent e, String fieldPrefix){
        if(null!= e.getLproductAdded() && !e.getLproductAdded().isEmpty()){
            if(saveMap.containsKey(fieldPrefix+"prod::"+e.getLproductAdded())) {
                int qty = Integer.parseInt(saveMap.remove(fieldPrefix+"prod::"+e.getLproductAdded()));
                int sum = qty+e.getLproductQuantity();
                saveMap.put(fieldPrefix+"qty::",sum+"");
            }else{
                //TODO add appropriate value for prod field
                saveMap.put(fieldPrefix+"prod::"+e.getLproductAdded(),"");
                saveMap.put(fieldPrefix+"qty::"+e.getLproductAdded(),""+e.getLproductQuantity());
            }
        }
        if(null!= e.getRproductAdded() && !e.getRproductAdded().isEmpty()){
            if(saveMap.containsKey(fieldPrefix+"qty::"+e.getRproductAdded())) {
                int qty = Integer.parseInt(saveMap.remove(fieldPrefix+"qty::"+e.getRproductAdded()));
                int sum = qty+e.getRproductQuantity();
                saveMap.put(fieldPrefix+"qty::",sum+"");
            }else{
                //TODO add appropriate value for prod field
                saveMap.put(fieldPrefix+"prod::"+e.getRproductAdded(),"");
                saveMap.put(fieldPrefix+"qty::"+e.getRproductAdded(),""+e.getRproductQuantity());
            }
        }
        if(null!= e.getLproductRemoved() && !e.getLproductRemoved().isEmpty()){
            if(saveMap.containsKey(fieldPrefix+"qty::"+e.getLproductRemoved())) {
                int qty = Integer.parseInt(saveMap.remove(fieldPrefix+"qty::"+e.getLproductRemoved()));
                int sum = qty-e.getLproductQuantity();
                saveMap.put(fieldPrefix+"qty::",sum+"");
            }else{
                int sum  = -e.getLproductQuantity();
                saveMap.put(fieldPrefix+"qty::"+e.getLproductRemoved(),sum+"");
            }
        }
        if(null!= e.getRproductRemoved() && !e.getRproductRemoved().isEmpty()){
            if(saveMap.containsKey(fieldPrefix+"qty::"+e.getRproductRemoved())) {
                int qty = Integer.parseInt(saveMap.remove(fieldPrefix+"qty::"+e.getRproductRemoved()));
                int sum = qty-e.getRproductQuantity();
                saveMap.put(fieldPrefix+"qty::",sum+"");
            }else{
                int sum = -e.getRproductQuantity();
                saveMap.put(fieldPrefix+"qty::"+e.getRproductRemoved(),sum+"");
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
