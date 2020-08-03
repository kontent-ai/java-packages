package kentico.kontent.delivery.sample.dancinggoat.viewModels;

import kentico.kontent.delivery.sample.dancinggoat.models.Article;
import kentico.kontent.delivery.sample.dancinggoat.models.HeroUnit;
import kentico.kontent.delivery.sample.dancinggoat.models.Home;

import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel {
    public BannerViewModel banner;
    public LayoutViewModel layout;
    public LatestArticleViewModel latestArticles;

    public HomeViewModel(Home model) {
        this.layout = new LayoutViewModel();
        this.layout.title = model.getMetadataTwitterSite();

        this.banner = new BannerViewModel(model.getHeroUnit().get(0).castTo(HeroUnit.class));

        List<Article> articles = model.getArticles().stream().map(item -> item.castTo(Article.class)).collect(Collectors.toList());
        this.latestArticles = new LatestArticleViewModel(articles);
    }
}
