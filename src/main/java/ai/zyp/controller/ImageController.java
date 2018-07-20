package ai.zyp.controller;

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
    public Map<String,List<String>> getImages() {

        return imageService.getImages();

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public List<String> getOrderImages(@PathVariable("id") String id) {
        return imageService.getImages(id);
    }

}
