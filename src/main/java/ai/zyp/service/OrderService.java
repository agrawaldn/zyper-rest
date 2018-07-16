package ai.zyp.service;

import ai.zyp.domain.Order;
import ai.zyp.domain.OrderItem;

import java.util.*;

/**
 * Created by Dev Agrawal on 7/14/18.
 */

public class OrderService {

    private Map<Long,Order> orders;


    public OrderService(){
        this.orders = buildOrders();
    }

    public List<Order> getVerifyOrders() {
        return new ArrayList<Order>(orders.values());
    }

    public List<OrderItem> getOrderItems(Long orderId){
        Order order = orders.get(orderId);
        return order.getOrderItems();
    }

    public Order getOrder(Long orderId){
        return orders.get(orderId);
    }

    public void updateOrder(Order order){
        Order oldOrder = orders.remove(order.getOrderId());
        orders.put(order.getOrderId(),order);
    }

    private Map<Long,Order> buildOrders() {
        Map<Long,Order> orders = new HashMap();
        Date today = new Date();

        OrderItem item1 = new OrderItem(1L,"Chips",1);
        OrderItem item2 = new OrderItem(2L,"Cocacola",1);
        OrderItem item3 = new OrderItem(3L,"Orange juice",1);
        OrderItem item4 = new OrderItem(4L,"Sprite",1);
        OrderItem item5 = new OrderItem(5L,"Milk",1);
        OrderItem item6 = new OrderItem(6L,"Fanta",1);
        OrderItem item7 = new OrderItem(7L,"Cheese",1);
        OrderItem item8 = new OrderItem(8L,"Apple",1);
        OrderItem item9 = new OrderItem(9L,"Icecream",1);
        OrderItem item10 = new OrderItem(10L,"Chocolate",1);
        OrderItem item11 = new OrderItem(11L,"Yogurt",1);

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item11);

        Order od1 = buildOrder(1L, today, 49L, items);
        items = new ArrayList<>();
        items.add(item3);
        items.add(item4);
        items.add(item1);

        Order od2 = buildOrder(2L, today, 39L, items);
        items = new ArrayList<>();
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);

        Order od3 = buildOrder(3L, today, 29L, items);
        items = new ArrayList<>();
        items.add(item9);
        items.add(item10);
        items.add(item3);
        items.add(item5);

        Order od4 = buildOrder(4L, today,19L, items);

        orders.put(od1.getOrderId(),od1);
        orders.put(od2.getOrderId(),od2);
        orders.put(od3.getOrderId(),od3);
        orders.put(od4.getOrderId(),od4);

        return orders;

    }

    private Order buildOrder(Long orderId, Date orderDate, Long customerId, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderDate(Order._DATE_FORMAT.format(orderDate));
        order.setCustomerId(customerId);
        order.setOrderItems(orderItems);
        return order;
    }

}
