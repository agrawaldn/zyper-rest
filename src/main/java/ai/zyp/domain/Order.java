package ai.zyp.domain;

import ai.zyp.conf.AppProperties;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
public class Order {

    //public static SimpleDateFormat _DATE_FORMAT = new SimpleDateFormat();

    private String orderId;
    private String customerId;
    private String startTime;
    private String endTime;
    private String status;
    private double paid;
    private String orderDate;

    private List<OrderItem> orderItems;
    private List<OrderEvent> orderEvents;

    public List<OrderEvent> getOrderEvents() {
        return orderEvents;
    }

    public void setOrderEvents(List<OrderEvent> orderEvents) {
        this.orderEvents = orderEvents;
    }

    public Order(){
        orderItems = new ArrayList<OrderItem>();
        orderItems.add(new OrderItem("","",0));
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        Date date = new Date(Long.valueOf(this.startTime));
        SimpleDateFormat df = new SimpleDateFormat(AppProperties.getInstance().getDateTimeFormat());
        this.setOrderDate(df.format(date));
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void updateProdQuantity(String prod, int qty){
        this.getOrderItems().forEach(item ->{
            if(item.getProductDesc().equals(prod)){
                item.setQuantity(qty);
            }
        });
    }

    public void addOrderItem(OrderItem orderItem) {
        if(this.getOrderItems() == null){
            List<OrderItem> items = new ArrayList<OrderItem>();
            items.add(orderItem);
            this.setOrderItems(items);
        }else {
            boolean alreadyExists = false;
            for(int i=0;i<this.getOrderItems().size();i++){
                OrderItem item = this.getOrderItems().get(i);
                if(item.equals(orderItem)){
                    item.addQuantity(orderItem.getQuantity());
                    alreadyExists = true;
                }
            }
            if(!alreadyExists){
                this.getOrderItems().add(orderItem);
            }
        }
    }

    public String toString(){
        StringBuffer ret = new StringBuffer();
        ret.append(" Order ID: "+getOrderId());
        ret.append(" Customer ID: "+getCustomerId());
        ret.append(" Status: "+getStatus());
        this.getOrderItems().forEach(item->{
            ret.append(" Product: "+item.getProductDesc()+" Quantity: "+item.getQuantity());
        });
        this.getOrderEvents().forEach(event->{
            ret.append("move: "+event.getMovements()+" lshelf: "+event.getLshelf()+" rshelf: "
                    +event.getRshelf()+" lprod+: "+event.getLproductAdded()+" rprod+: "
                    +event.getRproductAdded()+" lprod-: "+event.getLproductRemoved()+" rprod-: "
                    +event.getRproductRemoved()+" lqty: "+event.getLproductQuantity()+"rqty"
                    +event.getRproductQuantity());
        });
        return ret.toString();
    }

}


