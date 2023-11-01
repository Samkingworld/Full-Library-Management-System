package com.groupproject.libraryManagementSystem.service.authenticationService;

import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailService implements UserDetailsService {
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("In the User details service");
        User user = new User();
        try{
            user =  userRepository.findByEmail(username);
        }
        catch (Exception e){
            throw new UsernameNotFoundException("User is not valid");
        }
        return user;
    }

}
