package com.groupproject.libraryManagementSystem;

import com.groupproject.libraryManagementSystem.model.userEntity.Role;
import com.groupproject.libraryManagementSystem.model.userEntity.Status;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.repository.RoleRepository;
import com.groupproject.libraryManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class LibraryManagementSystemApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;


	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Role role = new Role();

		if(roleRepository.findByAuthority("ADMIN").isEmpty()){
			role.setAuthority("ADMIN");
			roleRepository.save(role);

		}

		if(roleRepository.findByAuthority("USER").isEmpty()){
			role.setAuthority("USER");
			roleRepository.save(role);
		}
		Role adminRole = roleRepository.findByAuthority("ADMIN").get();

		List<User> adminAccount = userRepository.findByAuthorities(adminRole);

		if (adminAccount.isEmpty() || adminAccount.size() < 1){
			Set<Role> userRoleSet = new HashSet<>();
			userRoleSet.add(adminRole);
			User user = new User();
			user.setEmail("admin@samkinglibrary.com");
			user.setFirstName("admin");
			user.setLastName("admin");
			user.setAddress("Samkingworld Library");
			user.setDob(new Date(System.currentTimeMillis()));
			user.setAuthorities(userRoleSet);
			user.setStatus(Status.VERIFIED);
			user.setPhoneNumber("09030000001");
			user.setFavoriteWord("admin");
			user.setPassword(passwordEncoder.encode("admin"));
			userRepository.save(user);
		}

	}

}
