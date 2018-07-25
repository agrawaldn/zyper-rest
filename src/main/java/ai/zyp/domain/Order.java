package ai.zyp.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
public class Order {

    public static SimpleDateFormat _DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private String orderId;
    private String customerId;
    private String startTime;
    private String endTime;
    private String status;
    private double paid;
    private String orderDate;

    private List<OrderItem> orderItems;

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
        Date date = new Date(Long.valueOf(endTime));
        this.setOrderDate(Order._DATE_FORMAT.format(date));
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
        ret.append(" Customer ID: "+getCustomerId());
        ret.append(" Status: "+getStatus());
        this.getOrderItems().forEach(item->{
            ret.append(" Product: "+item.getProductDesc()+" Quantity: "+item.getQuantity());
        });
        return ret.toString();
    }

}
