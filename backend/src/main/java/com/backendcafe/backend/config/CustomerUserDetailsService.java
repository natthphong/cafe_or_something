package com.backendcafe.backend.config;

import com.backendcafe.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    final  UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private com.backendcafe.backend.entity.User userDetail;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}",username);
        userDetail=userRepository.findByEmailId(username);
        if(!Objects.isNull(userDetail)){
            return new User(userDetail.getEmail() , userDetail.getPassword() , new ArrayList<>());
        }else{
           throw new UsernameNotFoundException("User not found");
        }
    }
    public com.backendcafe.backend.entity.User getUserDetail(){
        return userDetail;
    }
}
