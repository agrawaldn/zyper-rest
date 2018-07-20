package ai.zyp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dev Agrawal on 7/18/18.
 */
public class OrderImage {

    Map<String,List<String>> imageMap;

    public OrderImage(){
        imageMap = new HashMap();
    }

    public Map<String, List<String>> getImageMap() {
        return imageMap;
    }

    public void setImageMap(Map<String, List<String>> imageMap) {
        this.imageMap = imageMap;
    }

    public List<String> getImageList(String cameraId){
        return imageMap.get(cameraId);
    }

    public void putImageList(String cameraId, List<String> imageList){
        imageMap.put(cameraId, imageList);
    }
}
