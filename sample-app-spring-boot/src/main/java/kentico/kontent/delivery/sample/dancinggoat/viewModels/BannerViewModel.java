package kentico.kontent.delivery.sample.dancinggoat.viewModels;

import kontent.ai.delivery.Asset;
import kentico.kontent.delivery.sample.dancinggoat.models.HeroUnit;

public class BannerViewModel {
    public String primaryText;

    public String secondaryText;

    public Asset background;

    BannerViewModel(HeroUnit unit) {
        this.primaryText = unit.getTitle();
        this.secondaryText = unit.getMarketingMessage();
        this.background = unit.getImage().get(0);
    }
}
