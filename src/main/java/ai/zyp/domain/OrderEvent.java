package ai.zyp.domain;

import ai.zyp.conf.AppProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dev Agrawal on 7/27/18.
 */
public class OrderEvent {

    String camera;
    String timestamp;
    String movements;

    String lproductAdded;
    String lproductRemoved;
    int lproductQuantity;

    String rproductAdded;
    String rproductRemoved;
    int rproductQuantity;
    String lshelf;
    String rshelf;
    String origTS;

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMovements() {
        return movements;
    }

    public void setMovements(String movements) {
        this.movements = movements;
    }

    public String getLproductAdded() {
        return lproductAdded;
    }

    public void setLproductAdded(String lproductAdded) {
        this.lproductAdded = lproductAdded;
    }

    public String getLproductRemoved() {
        return lproductRemoved;
    }

    public void setLproductRemoved(String lproductRemoved) {
        this.lproductRemoved = lproductRemoved;
    }

    public int getLproductQuantity() {
        return lproductQuantity;
    }

    public void setLproductQuantity(int lproductQuantity) {
        this.lproductQuantity = lproductQuantity;
    }

    public String getRproductAdded() {
        return rproductAdded;
    }

    public void setRproductAdded(String rproductAdded) {
        this.rproductAdded = rproductAdded;
    }

    public String getRproductRemoved() {
        return rproductRemoved;
    }

    public void setRproductRemoved(String rproductRemoved) {
        this.rproductRemoved = rproductRemoved;
    }

    public int getRproductQuantity() {
        return rproductQuantity;
    }

    public void setRproductQuantity(int rproductQuantity) {
        this.rproductQuantity = rproductQuantity;
    }

    public String getLshelf() {
        return lshelf;
    }

    public void setLshelf(String lshelf) {
        this.lshelf = lshelf;
    }

    public String getRshelf() {
        return rshelf;
    }

    public void setRshelf(String rshelf) {
        this.rshelf = rshelf;
    }

    public String getOrigTS() {
        return origTS;
    }

    public void setOrigTS(String origTS) {
        this.origTS = origTS;
    }

    //    public String getEpochTimestamp(){
//        if(timestamp == null) return null;
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat(AppProperties.getInstance().getDateTimeFormat());
//            Date dt = sdf.parse(timestamp);
//            long epoch = dt.getTime();
//            return String.valueOf(epoch);
//        } catch(ParseException e) {
//            return null;
//        }
//    }


}