package ai.zyp.domain;

import java.util.List;

/**
 * Created by Dev Agrawal on 7/28/18.
 */
public class Shelf {
    private String id;
    private String defaultProduct;
    List<ShelfCoordinate> shelfCoordinates;

    public Shelf(String id, String defaultProduct){
        this.id = id;
        this.defaultProduct = defaultProduct;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultProduct() {
        return defaultProduct;
    }

    public void setDefaultProduct(String defaultProduct) {
        this.defaultProduct = defaultProduct;
    }

    public List<ShelfCoordinate> getShelfCoordinates() {
        return shelfCoordinates;
    }

    public void setShelfCoordinates(List<ShelfCoordinate> shelfCoordinates) {
        this.shelfCoordinates = shelfCoordinates;
    }
}
