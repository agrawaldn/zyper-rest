package ai.zyp.domain;

import java.util.List;

/**
 * Created by Dev Agrawal on 7/20/18.
 */
public class CameraImage {
    private String cameraId;
    private List<ImageDetail> imageDetails;

    public List<ImageDetail> getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(List<ImageDetail> imageDetails) {
        this.imageDetails = imageDetails;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

}
