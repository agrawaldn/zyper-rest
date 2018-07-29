package ai.zyp.service;

import ai.zyp.DAO.Redis;
import ai.zyp.domain.CameraImage;
import ai.zyp.domain.ImageDetail;
import ai.zyp.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Created by Dev Agrawal on 7/14/18.
 */


public class ImageService {

    private Redis db;
    private OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ImageService(){
        db = new Redis();
        orderService = new OrderService();
    }


    public List<CameraImage> getImages(String orderId) {
        List<CameraImage> ret = new ArrayList();
        Order order = orderService.getOrder(orderId,null, false);
        List<String> cameraKeyList = db.getMatchingKeys("cam-v2::*",50);
        logger.debug("Number of cameras returned = "+cameraKeyList.size());
        Map<String,Map<String,String>> trackData = getTrackCoords(orderId);
        //for each camera
        cameraKeyList.forEach(key->{
            Map<String,String> camConfigMap = (Map<String,String>) db.fetchData(key,"Map");
            String cameraId = camConfigMap.get("serial");
            Map<String, String> coordMap = trackData.get(cameraId);

            Set<String> imageTimestampSet = db.zrangeByScore("frame::"+cameraId,
                    Long.valueOf(order.getStartTime()),
                    Long.valueOf(order.getEndTime()),10000);
            logger.debug("Number of images returned for "+cameraId+" = "+imageTimestampSet.size());
            List<ImageDetail> imageDetailList = generateImageDetails(
                    imageTimestampSet,camConfigMap.get("url_template"),coordMap);
            CameraImage img = new CameraImage();
            img.setCameraId(cameraId);
            img.setImageDetails(imageDetailList);
            ret.add(img);
        });

        return ret;
    }
    private List<ImageDetail> generateImageDetails(Set<String> imageTimestampSet,
                                                   String urlTemplate, Map<String, String> coordMap){
        List<ImageDetail> imageDetails = new ArrayList();
        imageTimestampSet.forEach(ts-> {
            ImageDetail imageDetail = new ImageDetail();
            String arr[] = ts.split(":");
            String secs = arr[0];
            String msecs = arr[1];
            String url = urlTemplate.replaceFirst("\\{secs\\}", secs).replaceFirst("\\{msecs\\}", msecs);
            //logger.debug("Added image at = "+url);
            imageDetail.setImageURL(url);
            String timestamp = ts.replaceFirst(":","");
            imageDetail.setTimestamp(timestamp);
            if (null !=coordMap && coordMap.containsKey(timestamp)){
                String[] coordinates;
                coordinates = coordMap.get(timestamp).split(":");
                imageDetail.setTx(Double.parseDouble(coordinates[0]));
                imageDetail.setTy(Double.parseDouble(coordinates[1]));
                imageDetail.setBx(Double.parseDouble(coordinates[2]));
                imageDetail.setBy(Double.parseDouble(coordinates[3]));
            }else{
                imageDetail.setTx(0);
                imageDetail.setTy(0);
                imageDetail.setBx(0);
                imageDetail.setBy(0);
            }
            imageDetails.add(imageDetail);
        });
        return imageDetails;
    }
    private Map<String,Map<String,String>> getTrackCoords(String orderId){
        List<String> trackList = new ArrayList<>();
        trackList = (List<String>)db.fetchData("track-v2::"+orderId,"List");
        Map<String,Map<String,String>> ret = new HashMap<>();
        Map<String,String> tsCoordMap = new HashMap<>();
        trackList.forEach(track->{
            String[] lst = track.split(":");

            //logger.debug(" for timestamp:"+lst[0]);
            tsCoordMap.put(lst[0],lst[18]+":"+lst[19]+":"+lst[20]+":"+lst[21]);
            if(!ret.containsKey(lst[1])){
                ret.put(lst[1],tsCoordMap);
            }
        });
        logger.debug("added track information for "+ret.size()+" cameras");
        logger.debug("added "+ret.get("752112070219").size()+" track information for camera 752112070219");
        return ret;
    }

    public static void main(String[] args){
        ImageService service = new ImageService();
        List<CameraImage> items = service.getImages("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX "+items.size());
        items.forEach(item->System.out.println("cameraId = "+item.getCameraId()+
                " image count = "+item.getImageDetails().size()));
    }
}
