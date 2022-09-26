package kentico.kontent.delivery.sample.dancinggoat.controllers;

import kontent.ai.delivery.DeliveryClient;
import kentico.kontent.delivery.sample.dancinggoat.models.Home;
import kentico.kontent.delivery.sample.dancinggoat.viewModels.HomeViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class HomeController {
    @Autowired
    DeliveryClient deliveryClient;

    @GetMapping("/")
    String index(Model model) throws ExecutionException, InterruptedException {

        Home home = deliveryClient.getItem("home", Home.class)
                .toCompletableFuture()
                .get();

        HomeViewModel viewModel = new HomeViewModel(home);
        model.addAttribute("model", viewModel);
        return "home";
    }
}
