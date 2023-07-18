package tiketihub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tiketihub.data.OrganizedEventRepository;
import tiketihub.data.RoleRepository;
import tiketihub.data.UserRepository;
import tiketihub.entity.OrganizedEvent;
import tiketihub.entity.Role;
import tiketihub.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tiketihub.controller.baseMappingConfig.base;

@Controller
@Slf4j
public class RegistrationController {
    private final UserRepository userRepo;
    private final OrganizedEventRepository organizedEventRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;


    public RegistrationController(UserRepository userRepo, OrganizedEventRepository organizedEventRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.organizedEventRepo = organizedEventRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping(base+"/createAccount")
    public String getForm(Model model) {
        Role role1 = new Role("USER");
        Role role2 = new Role("GUEST");
        Role role3 = new Role("HOST");
        Role role4 = new Role("CO-HOST");

        /*roleRepo.save(role1);
        roleRepo.save(role2);
        roleRepo.save(role3);
        roleRepo.save(role4);*/
        User user = new User();

        List<User.Gender> genderList = new ArrayList<>(Arrays.asList(User.Gender.values()));

        model.addAttribute("user",user);
        model.addAttribute("user",genderList);

        return "createAccount";
    }
    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error",required = false) String error) {
        log.info("Error message (" + error + ")");
        model.addAttribute("errorMessage", (error != null) ? "Invalid username or password" : "");

        return "/login";
    }
    @PostMapping (base+"/createAccount")
    public String processForm(@Validated @ModelAttribute("user") User user) {
        user.setUsername((user.getFirstName()+user.getLastName()).toLowerCase());
        if (!user.getPassword().contentEquals(user.getConfirmPassword()) ||
                user.getPassword().isEmpty()) return "/createAccount";

        user.encodePassword(passwordEncoder);

        log.info(user.toString());
        userRepo.save(user);
        OrganizedEvent currentUser = new OrganizedEvent();
        currentUser.setUser(user);
        currentUser.setUsername(user.getUsername());
        currentUser.setEncodedPassword(user.getPassword());

        Role userRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role does not exist"));
        currentUser.setRoles(List.of(userRole));
        organizedEventRepo.save(currentUser);

        return "redirect:/login";
    }

    @GetMapping(base+"/details")
    public String getDetails(@AuthenticationPrincipal OrganizedEvent activeUser,
                             Model model) {
         model.addAttribute("currentUser", activeUser.getUser());
        log.info(activeUser.getUser().toString());

        return "details";
    }

}

