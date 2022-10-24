package kontent.ai.delivery.sample.dancinggoat.controllers;

import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryParameterBuilder;
import kontent.ai.delivery.sample.dancinggoat.models.AboutUs;
import kontent.ai.delivery.sample.dancinggoat.viewModels.AboutUsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class AboutUsController {
    @Autowired
    DeliveryClient deliveryClient;

    @GetMapping("/about-us")
    String aboutUs(Model model) throws ExecutionException, InterruptedException {
        AboutUs aboutUs = deliveryClient
                .getItems(AboutUs.class, DeliveryParameterBuilder
                        .params()
                        .filterEquals("elements.url_pattern", "about-us")
                        .build())
                .toCompletableFuture()
                .get()
                .get(0);

        AboutUsViewModel viewMode = new AboutUsViewModel(aboutUs);

        model.addAttribute("model", viewMode);
        return "about-us";
    }
}
