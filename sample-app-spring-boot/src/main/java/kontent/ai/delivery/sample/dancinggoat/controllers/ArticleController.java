package kontent.ai.delivery.sample.dancinggoat.controllers;

import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryParameterBuilder;
import kontent.ai.delivery.sample.dancinggoat.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class ArticleController {
    @Autowired
    DeliveryClient deliveryClient;

    @GetMapping("/articles")
    public String getArticles(Model model) throws ExecutionException, InterruptedException {
        List<Article> articles = deliveryClient
                .getItems(Article.class, DeliveryParameterBuilder
                        .params()
                        .page(0, 10)
                        .orderByDesc("elements.post_date")
                        .build())
                .toCompletableFuture()
                .get();

        model.addAttribute("articles", articles);
        return "articles";
    }

    @GetMapping("/articles/{pattern}")
    public String getArticle(Model model, @PathVariable("pattern") String pattern) throws ExecutionException, InterruptedException {
        Article article = deliveryClient
                .getItems(Article.class, DeliveryParameterBuilder
                        .params()
                        .filterEquals("elements.url_pattern", pattern)
                        .build())
                .toCompletableFuture()
                .get()
                .get(0);
        model.addAttribute("article", article);
        return "article";
    }
}
