package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.CameraImage;
import ai.zyp.domain.ImageDetail;
import ai.zyp.domain.Order;
import ai.zyp.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Dev Agrawal on 7/28/18.
 */


public class LookupService {

    private Redis db;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LookupService(){
        db = new Redis("localhost", Integer.parseInt("0"));
    }

    public List<Product> getAllProducts(){
        List<Product> products = new ArrayList();

        List<String> keys = db.getMatchingKeys("prod::" + "*", 1000);
        logger.debug("Number of products in lookup = " + keys.size());
        keys.forEach(key -> {
            Map<String, String> prodMap = (Map<String, String>)db.fetchData(key, "Map");
            String productId = key.replaceFirst("prod::","");
            String productDesc = prodMap.get("name");
            if(productDesc.length()>27){
                productDesc = productDesc.substring(0,26)+"...";
            }
            Product prod = new Product(productId, productDesc);
            products.add(prod);
        });
        return products;
    }
}
