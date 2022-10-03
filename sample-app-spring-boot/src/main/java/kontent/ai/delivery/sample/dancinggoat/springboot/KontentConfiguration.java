package kontent.ai.delivery.sample.dancinggoat.springboot;

import kontent.ai.delivery.DeliveryClient;
import kontent.ai.delivery.DeliveryOptions;
import kontent.ai.delivery.Header;
import kontent.ai.delivery.InlineContentItemsResolver;
import kontent.ai.delivery.sample.dancinggoat.models.Tweet;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

@Configuration
public class KontentConfiguration {

    // https://github.com/Kentico/Home/wiki/Guidelines-for-Kontent-related-tools#analytics
    private static final String TRACKING_HEADER_NAME = "X-KC-SOURCE";
    private static final String TRACKING_HEADER_VALUE = "kentico.kontent.delivery.sample.dancinggoat.java.springboot;1.0.0";

    @Bean
    public DeliveryClient deliveryClient() {
        DeliveryClient client = new DeliveryClient(
                DeliveryOptions
                        .builder()
                        .projectId("975bf280-fd91-488c-994c-2f04416e5ee3")
                        .customHeaders(Arrays.asList(
                                new Header(TRACKING_HEADER_NAME, TRACKING_HEADER_VALUE)
                        ))
                        .build()
        );

        client.scanClasspathForMappings("kentico.kontent.delivery.sample.dancinggoat.models");

        client.registerInlineContentItemsResolver(new InlineContentItemsResolver<Tweet>() {
            @Override
            public String resolve(Tweet item) {
                String theme = item.getTheme().get(0).getName().toLowerCase();
                Boolean hideThread = item
                        .getDisplayOptions()
                        .stream()
                        .filter(o -> o.getCodename().equals("hide_thread"))
                        .findFirst()
                        .isPresent();
                Boolean hideMedia = item
                        .getDisplayOptions()
                        .stream()
                        .filter(o -> o.getCodename().equals("hide_media"))
                        .findFirst()
                        .isPresent();

                String options = String.format("&hide_thread=%b&hide_media=%b", hideThread, hideMedia);
                String tweetLink = item.getTweetLink();
                String url = String.format("https://publish.twitter.com/oembed?url=%s&theme=%s%s", tweetLink, theme, options);

                RestTemplate restTemplate = new RestTemplate();
                String responseBody = restTemplate.getForObject(url, String.class);
                Map<String, Object> object = JsonParserFactory.getJsonParser().parseMap(responseBody);
                return (String) object.get("html");
            }
        });
        return client;
    }
}
