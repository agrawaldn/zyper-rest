package ai.zyp.domain;

import java.util.List;

/**
 * Created by Dev Agrawal on 7/20/18.
 */
public class CameraImage {
    private String cameraId;
    private List<String> images;
    
    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }




}
