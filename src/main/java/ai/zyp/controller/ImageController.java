package ai.zyp.controller;

import ai.zyp.domain.CameraImage;
import ai.zyp.domain.Order;
import ai.zyp.service.ImageService;
import ai.zyp.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dev Agrawal on 7/17/18.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/images")
public class ImageController {

    ImageService imageService = new ImageService();


    @RequestMapping(method = RequestMethod.GET)
    public List<CameraImage> getImages() {
        List<CameraImage> imageList = new ArrayList<>();
        Map<String,List<String>> imageMap = imageService.getImages();
        imageMap.forEach((k,v) -> {
            CameraImage image = new CameraImage();
            image.setCameraId(k);
            image.setImages(v);
            imageList.add(image);
        });
        return imageList;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CameraImage getOrderImages(@PathVariable("id") String id) {
        CameraImage image = new CameraImage();
        image.setCameraId(id);
        image.setImages(imageService.getImages(id));
        return image;
    }

}
