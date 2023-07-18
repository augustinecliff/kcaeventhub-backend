package tiketihub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tiketihub.data.RoleRepository;
import tiketihub.entity.Role;

@SpringBootApplication
public class TiketiHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiketiHubApplication.class, args);


    }

}
