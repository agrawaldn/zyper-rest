package ai.zyp.controller;

import ai.zyp.domain.Order;
import ai.zyp.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/lookup")
public class LookupController {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "product",method = RequestMethod.GET)
    public List<String> getProducts() {
        List<String> prodList = new ArrayList<>();
        prodList.add("Pepsi, 12 fl oz");
        prodList.add("Diet Coke, 12 fl oz");
        prodList.add("Suja Organic Uber Greens Juice");
        prodList.add("ProBar Meal Superfood Slam");
        prodList.add("Chips 20 oz");
        prodList.add("Vita coconut water");
        prodList.add("Dang Sticky-Rice Chips");
        prodList.add("Diet Snapple Lemon Tea");
        prodList.add("Noosa Blueberry Yogurt");

        return prodList;
    }



}