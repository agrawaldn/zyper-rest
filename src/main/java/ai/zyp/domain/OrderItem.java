package ai.zyp.domain;

/**
 * Created by Dev Agrawal on 7/14/18.
 */
public class OrderItem {

    private String productDesc;
    private String productId;
    private int quantity;

    public OrderItem(String productId, String product, int qty){
        this.productDesc = product;
        this.productId = productId;
        this.quantity = qty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity = this.quantity+quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean equals(OrderItem orderItem){
        if(orderItem !=null){
            if (this.productId.equals(orderItem.productId)
                    && this.productDesc.equals(orderItem.productDesc)) {
                return true;
            }
        }
        return false;
    }
    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

}