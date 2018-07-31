package ai.zyp.controller;

import ai.zyp.domain.Order;
import ai.zyp.domain.OrderItem;
import ai.zyp.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private OrderService service;

    OrderController() {
        this.service = new OrderService();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Order> getVerifyOrders() {
        return service.getVerifyOrders();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Order getOrder(@PathVariable("id") String id) {
        return service.getOrder(id,null, false);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Order saveVerifiedOrder(@RequestBody Order order) {
        logger.debug("PUT request received for order "+order.toString());
        service.saveVerifiedOrder(order);
        return order;
    }

}