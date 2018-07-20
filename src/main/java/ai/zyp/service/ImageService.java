package ai.zyp.service;

import ai.zyp.domain.OrderImage;
import java.io.File;
import java.util.*;

/**
 * Created by Dev Agrawal on 7/14/18.
 */


public class ImageService {
    public static String BASE_PATH = "/home/devendra/apache/camera/";
    public static String BASE_URL = "http://localhost/camera/";
    public static String CAMERA_1 = "728312070375";
    public static String CAMERA_2 = "745212070402";
    public static String CAMERA_3 = "752112070219";
    public static String CAMERA_4 = "819112072121";
    OrderImage orderImage;

    public ImageService(){
        orderImage = new OrderImage();
        buildData();
    }

    public List<String> getImages(String cameraId) {
        return orderImage.getImageList(cameraId);
    }

    public Map<String,List<String>> getImages() {
        return orderImage.getImageMap();
    }
    private void buildData() {

        orderImage.putImageList(CAMERA_1,buildData(CAMERA_1));
        orderImage.putImageList(CAMERA_2,buildData(CAMERA_2));
        orderImage.putImageList(CAMERA_3,buildData(CAMERA_3));
        orderImage.putImageList(CAMERA_4,buildData(CAMERA_4));

    }

    private List<String> buildData(String cameraId){
        List<String> imageList = new ArrayList();
        File folder = new File(BASE_PATH+cameraId);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                imageList.add(BASE_URL+cameraId+"/"+listOfFiles[i].getName());
                System.out.println(folder.getAbsolutePath()+listOfFiles[i].getName());
            }
        }
        return imageList;
    }

    public static void main(String[] args){
        ImageService service = new ImageService();
        List<String> items = service.getImages(CAMERA_1);
        items.forEach(item->System.out.println(item));
    }
}
