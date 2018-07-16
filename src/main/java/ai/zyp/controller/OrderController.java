package ai.zyp.controller;

import ai.zyp.domain.Order;
import ai.zyp.domain.OrderItem;
import ai.zyp.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/orders")
public class OrderController {


    private OrderService service;

    OrderController() {
        this.service = new OrderService();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Order> getVerifyOrders() {
        return service.getVerifyOrders();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Order getOrder(@PathVariable("id") Long id) {
        return service.getOrder(id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Order updateOrder(@RequestBody Order order) {
        service.updateOrder(order);
        return order;
    }

}