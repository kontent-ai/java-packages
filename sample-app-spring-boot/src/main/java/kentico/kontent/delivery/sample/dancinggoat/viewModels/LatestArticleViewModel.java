package kentico.kontent.delivery.sample.dancinggoat.viewModels;

import kentico.kontent.delivery.sample.dancinggoat.models.Article;

import java.util.List;

public class LatestArticleViewModel {
    public Article mainArticle;
    public List<Article> otherArticles;


    public LatestArticleViewModel(List<Article> articles) {
        this.mainArticle = articles.get(0);
        this.otherArticles = articles.subList(1, articles.size());
    }
}
