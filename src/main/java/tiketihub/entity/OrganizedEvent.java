package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//This will be the session controller
@Entity
@Table(name = "organized_event")
@Data
public class OrganizedEvent implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String organized_event_id;

    @ManyToOne(targetEntity = User.class)
    private User user;

    private String username;
    private String password;

    @ManyToMany
    @JoinTable(
            name = "organizedevent_event",
            joinColumns = @JoinColumn(name = "organized_event_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
    @ManyToMany(targetEntity = Role.class)
    private List<Role> roles;
    @ManyToOne(targetEntity = Token.class)
    private Token token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = null;
        /*if (roles.isEmpty())*/ authorities = List.of(new SimpleGrantedAuthority("USER"));

        /*else {
            for (Role roleList : roles) authorities.add(new SimpleGrantedAuthority(roleList.getName()));

            return authorities;
        }*/

        return authorities;
    }
    public void setEncodedPassword(String password) {
        this.password = password;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
