package kontent.ai.delivery.sample.dancinggoat.controllers;

import kontent.ai.delivery.ContentItem;
import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.sample.dancinggoat.models.Home;
import kontent.ai.delivery.sample.dancinggoat.springboot.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Controller
public class TestController implements Serializable {
    @Autowired
    DeliveryClient deliveryClient;

    @Autowired
    TestService testService;

    @GetMapping("/test")
    ResponseEntity<String>  getTest(Model model) throws ExecutionException, InterruptedException {

        ContentItem item = testService.fetchContentByCodeName("on_roasts", deliveryClient);

        ContentItem item2 = testService.fetchContentByCodeName("about_us", deliveryClient);

        return new ResponseEntity<>(item.getString("title") + " " + item2.getString("metadata__og_title"), HttpStatus.OK);
    }
}
