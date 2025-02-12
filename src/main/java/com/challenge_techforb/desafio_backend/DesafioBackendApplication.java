package com.challenge_techforb.desafio_backend;

import com.challenge_techforb.desafio_backend.persistence.entity.PermissionEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.RoleEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.RoleEnum;
import com.challenge_techforb.desafio_backend.persistence.entity.UserEntity;
import com.challenge_techforb.desafio_backend.persistence.repository.RoleRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class DesafioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioBackendApplication.class, args);
	}


	@Bean
	CommandLineRunner init(RoleRepository roleRepository) {
		return args -> {

	PermissionEntity createPermission = new PermissionEntity("CREATE");

	PermissionEntity readPermission = new PermissionEntity("READ");

	PermissionEntity updatePermission = new PermissionEntity("UPDATE");

	PermissionEntity deletePermission = new PermissionEntity("DELETE");

	PermissionEntity refactorPermission = new PermissionEntity("REFACTOR");

	RoleEntity roleAdmin = new RoleEntity(
			RoleEnum.ADMIN,
			Set.of(createPermission, readPermission, updatePermission, deletePermission)
	);

	RoleEntity roleUser = new RoleEntity(
			RoleEnum.USER,
			Set.of(createPermission, readPermission)
	);

	RoleEntity roleInvited = new RoleEntity(
			RoleEnum.INVITED,
			Set.of(readPermission)
	);

	RoleEntity roleDeveloper = new RoleEntity(
			RoleEnum.DEVELOPER,
			Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission)
	);

            roleRepository.saveAll(List.of(roleAdmin,roleDeveloper,roleInvited,roleUser));

    };
	}



}
