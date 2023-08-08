package tiketihub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // trying to use Async
public class TiketiHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiketiHubApplication.class, args);


    }

}
