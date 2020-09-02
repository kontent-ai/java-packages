package kentico.kontent.delivery.sample.dancinggoat.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "kentico.kontent.delivery.sample.dancinggoat.springboot", "kentico.kontent.delivery.sample.dancinggoat.controllers"
})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
