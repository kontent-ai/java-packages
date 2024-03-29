# Changes in v4 -> v5

* Thymeleaf default template location changed
  * `kontent/ai/templates/`, `META-INF/kontent/ai/templates/` - you need to move your templates, use custom path configuration
* Namespaces of the packages we changed from `kentico.kontent.delivery.*` to `kontent.ai.delivery.*`
* Prefix for Kontent.ai exceptions has been changed from `Kentico` to `Kontent`: 
  * `KontentError`, `KontentErrorException`, `KontentException`, `KontentIOException`, and `KontentRetryException`
* Android Sample apps changed the application id prefix to "kontent.ai.*"
* Packages are signed by the new PGP key with id `A5992A24`

# Changes in v3 -> v4

* Delivery client now returns `CompletionStage` and it is async by default
  * Internally [OkHttpClient](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/) is used to handle network requests
* Place for templates is by default
  * `kentico/templates/`, `META-INF/kentico/templates/`, `kentico/kontent/templates/`, `META-INF/kentico/kontent/templates/` (last two are new)
* Retry codes are now set statically in DeliveryClient: `408, 429, 500, 502, 503, 504`
  * If the retry response is not parsable to KenticoError.class - retry is not performed - https://kontent.ai/learn/reference/delivery-api#section/Errors/Resolving-errors
* Accessing linked items element data was simplified
  * from
  
    ```java
    List<String> relatedArticleItemCodename = 
      ((ModularContentElement) contentItem.getElements().get("related_article"))
        .getValue();

    if (!relatedArticlesItemCodenames.isEmpty()) {                 
      Article article = contentItem.getModularContent(relatedArticlesItemCodenames.get(0))
        .castTo(Article.class);                 
    }
    ```
  
  * to
  
    ```java
    ContentItem relatedArticle = contentItem.getLinkedItem("related_article");

    if (relatedArticle != null) {
        Article article = relatedArticle.castTo(Article.class);
    }
    ```
  
