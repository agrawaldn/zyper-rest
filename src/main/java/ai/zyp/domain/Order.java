package ai.zyp.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private List<OrderItem> orderItems;

    public Order(String orderId, Map<String,String> inputMap){
        this.setOrderId(orderId);
        inputMap.forEach((k,v)->{
            switch (k){
                case "customer_id": this.setCustomerId(v);
                break;
                case "status": this.setStatus(v);
                break;
                case "start": this.setStartTime(v);
                break;
                case "end": this.setEndTime(v);
                break;
                case "Paid": this.setPaid(Double.parseDouble(v));
                break;
                default:
                    if(k.startsWith("prod::")){
                        this.addOrderItem(new OrderItem(v,k.replaceFirst("prod::","")));
                    }
            }

        });
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
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    public void addOrderItem(OrderItem orderItem) {
        if(this.orderItems == null){
            orderItems = new ArrayList<OrderItem>();
        }
        orderItems.add(orderItem);
    }

}
