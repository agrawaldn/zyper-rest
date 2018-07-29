package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.*;
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
        db = new Redis();
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

    public List<Shelf> getAllShelves(){
        List<Shelf> shelves = new ArrayList();

        List<String> keys = db.getMatchingKeys("shelf-v2::" + "*", 1000);
        logger.debug("Number of shelves loaded = " + keys.size());
        keys.forEach(key -> {
            Map<String, String> shelfMap = (Map<String, String>)db.fetchData(key, "Map");
            String shelfId = key.replaceFirst("shelf-v2::","");
            String defaultProduct = shelfMap.get("default_product").replaceFirst("prod::","");
            Shelf shelf = new Shelf(shelfId,defaultProduct);
            populateShelf(shelf, shelfMap);
            shelves.add(shelf);
        });
        return shelves;
    }

    private void populateShelf(Shelf shelf, Map<String, String> shelfMap){
        List<ShelfCoordinate> coordinateList = new ArrayList();
        shelfMap.forEach((k,v)->{
            if(k.startsWith("cam::")){
                ShelfCoordinate coordinate = new ShelfCoordinate();
                coordinate.setCameraId(k.replaceFirst("cam::",""));
                populateCoordinate(coordinate,v);
                coordinateList.add(coordinate);
            }
        });
        shelf.setShelfCoordinates(coordinateList);
    }

    private void populateCoordinate(ShelfCoordinate c, String s){
        //{'x':[1095,1043,1030,1092],'y':[449,438,493,505]}
        String[] arr = s.split(",");
        //c.setX1(Double.parseDouble(arr[0].replaceFirst("'x':\\[","")));
        c.setX1(Double.parseDouble(arr[0].split("\\[")[1]));
        c.setX2(Double.parseDouble(arr[1]));
        c.setX3(Double.parseDouble(arr[2]));
        c.setX4(Double.parseDouble(arr[3].replace("]","")));
        c.setY1(Double.parseDouble(arr[4].split("\\[")[1]));
        c.setY2(Double.parseDouble(arr[5]));
        c.setY3(Double.parseDouble(arr[6]));
        c.setY4(Double.parseDouble(arr[7].replace("]}","")));

    }

}
