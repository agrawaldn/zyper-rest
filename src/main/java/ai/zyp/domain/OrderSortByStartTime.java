package ai.zyp.domain;

import java.util.Comparator;

public class OrderSortByStartTime implements Comparator<Order> {

    @Override
    public int compare(Order order, Order t1) {
        int comp = (int)(Long.valueOf(t1.getStartTime())-Long.valueOf(order.getStartTime()));
        return comp;
    }
}
