package ai.zyp.controller;

import ai.zyp.domain.Order;
import ai.zyp.domain.Product;
import ai.zyp.domain.Shelf;
import ai.zyp.service.LookupService;
import ai.zyp.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/lookup")
public class LookupController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private LookupService service = new LookupService();

    @RequestMapping(value = "product",method = RequestMethod.GET)
    public List<Product> getProducts() {
        return service.getAllProducts();
    }

    @RequestMapping(value = "shelf",method = RequestMethod.GET)
    public List<Shelf> getShelves() {
        return service.getAllShelves();
    }

}