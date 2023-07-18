package tiketihub.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tiketihub.data.OrganizedEventRepository;
import tiketihub.data.UserRepository;
import tiketihub.entity.OrganizedEvent;

@Service
@Slf4j
public class UserDetailsServiceImp implements UserDetailsService {

    private final OrganizedEventRepository organizedEventRepo;
    private final UserRepository userRepo;

    @Autowired
    public UserDetailsServiceImp(OrganizedEventRepository organizedEventRepo, UserRepository userRepo) {
        this.organizedEventRepo = organizedEventRepo;
        this.userRepo = userRepo;
    }

    @Override
    public OrganizedEvent loadUserByUsername(String username) throws UsernameNotFoundException {


        if (organizedEventRepo.findByUsername(username).isPresent()) {
            log.info("\n Username:" + username + "is logging in");
            return organizedEventRepo.findByUsername(username).get();
        }

        log.info("User " + username + "does not exist");
        return organizedEventRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + "does not exist"));

    }
}
