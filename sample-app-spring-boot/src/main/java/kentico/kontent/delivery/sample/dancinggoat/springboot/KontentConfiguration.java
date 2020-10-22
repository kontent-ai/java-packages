package kentico.kontent.delivery.sample.dancinggoat.springboot;

import kentico.kontent.delivery.ContentItem;
import kentico.kontent.delivery.DeliveryClient;
import kentico.kontent.delivery.InlineContentItemsResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KontentConfiguration {
    @Bean
    public DeliveryClient deliveryClient() {
        DeliveryClient client = new DeliveryClient("975bf280-fd91-488c-994c-2f04416e5ee3");
        client.scanClasspathForMappings("kentico.kontent.delivery.sample.dancinggoat.models");

        client.registerDefaultInlineContentItemsResolver(new InlineContentItemsResolver<ContentItem>() {
            @Override
            public String resolve(ContentItem data) {
                // TODO think about default template engine configuration

                // Current priority --> strongly typed Inline Content Item Resolver, Template Engine resolver, Default resolver, Nothing

                return "<h1>" + data.getSystem().getType() + "</h1>";
            }
        });

//        client.registerInlineContentItemsResolver(new InlineContentItemsResolver<Tweet>() {
//            @Override
//            public String resolve(Tweet item) {
//                String theme = item.getTheme().get(0).getName().toLowerCase();
//                Boolean hideThread = item
//                        .getDisplayOptions()
//                        .stream()
//                        .filter(o -> o.getCodename().equals("hide_thread"))
//                        .findFirst()
//                        .isPresent();
//                Boolean hideMedia = item
//                        .getDisplayOptions()
//                        .stream()
//                        .filter(o -> o.getCodename().equals("hide_media"))
//                        .findFirst()
//                        .isPresent();
//
//                String options = String.format("&hide_thread=%b&hide_media=%b", hideThread, hideMedia);
//                String tweetLink = item.getTweetLink();
//                String url = String.format("https://publish.twitter.com/oembed?url=%s&theme=%s%s", tweetLink, theme, options);
//
//                RestTemplate restTemplate = new RestTemplate();
//                String responseBody = restTemplate.getForObject(url, String.class);
//                Map<String, Object> object = JsonParserFactory.getJsonParser().parseMap(responseBody);
//                return (String) object.get("html");
//            }
//        });
        return client;
    }
}
