package ai.zyp.domain;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
public class OrderItem {

    private String productDesc;
    private String productId;

    public OrderItem(String productId, String product){
        this.productDesc = product;
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

}