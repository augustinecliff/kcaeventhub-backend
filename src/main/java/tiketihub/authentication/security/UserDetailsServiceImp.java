package tiketihub.authentication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tiketihub.user.User;
import tiketihub.user.UserRepository;
import tiketihub.user.UserSession;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = userRepo.findByEmail(email).
                    orElseThrow( () ->
                            new UsernameNotFoundException("A user with the specified email does not exist")) ;

        return new UserSession(
                user.getEmail(),
                user.getPassword(),
                user.getUserRole(),
                user.getId());
    }
}
