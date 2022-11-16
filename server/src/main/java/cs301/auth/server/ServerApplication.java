package cs301.auth.server;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
// import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cs301.auth.server.role.Role;
import cs301.auth.server.role.RoleService;
import cs301.auth.server.user.*;
import cs301.auth.server.utils.CreateKey;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		// CreateKey.genRSAKeyPairAndSaveToFile();
		SpringApplication.run(ServerApplication.class, args);
	}

	// @Bean
	// PasswordEncoder PasswordEncoder(){
	// return new BCryptPasswordEncoder();
	// }

	@Bean
	CommandLineRunner run(UserService userService, RoleService roleService) {
		return args -> {
			roleService.addRole(new Role(null, "ROLE_USER"));
			roleService.addRole(new Role(null, "ROLE_ADMIN"));
			roleService.addRole(new Role(null, "ROLE_SUPER_ADMIN"));
			userService.saveUser(new User("8cecd1af-6c38-4186-9fe7-ba1cf15a7379", "russel.stephan@kihn.name",
					"password", "Stephan", "Russel", true, "1989-09-04", false,new ArrayList<>()));
			userService.saveUser(new User("7eb073af-1377-409d-90ef-55db627852d2", "pagac_vince@yost.io",
					"password", "Vince", "Pagac", true, "1963-05-21",true, new ArrayList<>()));
			userService.saveUser(new User("7eb073af-1377-409d-90ef-55db627852d3", "leeshuoan38@gmail.com",
					"password", "Shuoan", "Lee", false, "1999-11-11",false, new ArrayList<>()));

			// // userService.addRoleToUser("timymama", "ROLE_ADMIN");
			// userService.addRoleToUser("latricia_schulist@abbott.com", "ROLE_ADMIN");
			// userService.addRoleToUser("latricia_schulist@abbott.com",
			// "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("russel.stephan@kihn.name", "ROLE_USER");
			userService.addRoleToUser("russel.stephan@kihn.name", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("pagac_vince@yost.io", "ROLE_ADMIN");
			userService.addRoleToUser("pagac_vince@yost.io", "ROLE_USER");
			userService.addRoleToUser("leeshuoan38@gmail.com", "ROLE_USER");
		};
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
