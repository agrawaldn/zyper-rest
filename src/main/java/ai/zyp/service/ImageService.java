package ai.zyp.service;

import ai.zyp.DAO.Redis;
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
        db = new Redis("localhost", Integer.parseInt("0"));
        orderService = new OrderService();
    }


    public Map<String,List<String>> getImages(String orderId) {
        Map<String,List<String>> ret = new HashMap();
        Order order = orderService.getOrder(orderId);
        List<String> cameraKeyList = db.getMatchingKeys("cam-v2::*",50);
        logger.debug("Number of cameras returned = "+cameraKeyList.size());
        cameraKeyList.forEach(key->{
            Map<String,String> camConfigMap = (Map<String,String>) db.fetchData(key,"Map");
            String urlTemplate = camConfigMap.get("url_template");
            String cameraId = camConfigMap.get("serial");
            Set<String> imageTimestampSet = db.zrangeByScore("frame::"+cameraId,
                    Long.valueOf(order.getStartTime()),
                    Long.valueOf(order.getEndTime()),10000);
            logger.debug("Number of images returned for "+cameraId+" = "+imageTimestampSet.size());
            List<String> urlList = new ArrayList();
            imageTimestampSet.forEach(ts->{
                String arr[] = ts.split(":");
                String secs = arr[0];
                String msecs = arr[1];
                String url = urlTemplate.replaceFirst("\\{secs\\}",secs).replaceFirst("\\{msecs\\}",msecs);
                logger.debug("Added image at = "+url);
                urlList.add(url);
            });
            ret.put(cameraId, urlList);
        });

        return ret;
    }

    public static void main(String[] args){
        ImageService service = new ImageService();
        Map<String,List<String>> items = service.getImages("o-d5d30429-5ac0-4c05-8d5a-f7bc2fd5f75f");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX "+items.size());
        items.forEach((k,v)->System.out.println("cameraId = "+k+" image count = "+v.size()));
    }
}
