package kontent.ai.delivery.sample.dancinggoat.viewModels;

import kontent.ai.delivery.sample.dancinggoat.models.AboutUs;
import kontent.ai.delivery.sample.dancinggoat.models.FactAboutUs;

import java.util.List;
import java.util.stream.Collectors;

public class AboutUsViewModel {
    public LayoutViewModel layout;
    public List<FactAboutUs> facts;

    public AboutUsViewModel(AboutUs aboutUs) {
        this.layout = new LayoutViewModel();
        this.layout.title = aboutUs.getMetadataTwitterSite();

        this.facts = aboutUs
                .getFacts()
                .stream()
                .map(item -> item.castTo(FactAboutUs.class))
                .collect(Collectors.toList());
    }
}
