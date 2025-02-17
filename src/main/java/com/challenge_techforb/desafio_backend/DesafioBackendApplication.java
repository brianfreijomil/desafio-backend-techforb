package com.challenge_techforb.desafio_backend;

import com.challenge_techforb.desafio_backend.controller.dto.request.PlantIn;
import com.challenge_techforb.desafio_backend.controller.dto.request.ReadingIn;
import com.challenge_techforb.desafio_backend.controller.dto.request.SensorIn;
import com.challenge_techforb.desafio_backend.controller.dto.response.PlantInfoOut;
import com.challenge_techforb.desafio_backend.controller.dto.response.SensorOut;
import com.challenge_techforb.desafio_backend.persistence.entity.PermissionEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.RoleEntity;
import com.challenge_techforb.desafio_backend.persistence.entity.RoleEnum;
import com.challenge_techforb.desafio_backend.persistence.entity.UserEntity;
import com.challenge_techforb.desafio_backend.persistence.repository.RoleRepository;
import com.challenge_techforb.desafio_backend.persistence.repository.UserRepository;
import com.challenge_techforb.desafio_backend.service.PlantService;
import com.challenge_techforb.desafio_backend.service.SensorsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class DesafioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesafioBackendApplication.class, args);
	}


	@Bean
	CommandLineRunner init(PlantService plantService, SensorsService sensorsService, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
				Set.of(createPermission, updatePermission, deletePermission, readPermission)
		);

		RoleEntity roleInvited = new RoleEntity(
				RoleEnum.INVITED,
				Set.of(readPermission)
		);

		RoleEntity roleDeveloper = new RoleEntity(
				RoleEnum.DEVELOPER,
				Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission)
		);

		UserEntity u1 = UserEntity.builder()
				.email("usertest@hotmail.com")
				.username("usertest")
				.password(passwordEncoder.encode("Test12345@"))
				.roles(List.of(roleDeveloper))
				.created_at(LocalDateTime.now())
				.isEnabled(true)
				.build();

		UserEntity u2 = UserEntity.builder()
				.email("test12345@hotmail.com")
				.username("usertest2")
				.password(passwordEncoder.encode("Test12345@"))
				.roles(List.of(roleAdmin))
				.created_at(LocalDateTime.now())
				.isEnabled(true)
				.build();

			UserEntity u3 = UserEntity.builder()
					.email("emailtest@hotmail.com")
					.username("usernamet3")
					.password(passwordEncoder.encode("Test12345@"))
					.roles(List.of(roleInvited))
					.created_at(LocalDateTime.now())
					.isEnabled(true)
					.build();

			UserEntity u4 = UserEntity.builder()
					.email("usuariosimple@hotmail.com")
					.username("usuariosimple")
					.password(passwordEncoder.encode("Test12345@"))
					.roles(List.of(roleUser))
					.created_at(LocalDateTime.now())
					.isEnabled(true)
					.build();

		userRepository.saveAll(List.of(u1,u2,u3,u4));


		PlantInfoOut p1 = plantService.createPlant(new PlantIn("Tandil", "Argentina"), "usertest");
		plantService.createPlant(new PlantIn("Rauch","Argentina"),"usertest");
		plantService.createPlant(new PlantIn("Olavarria","Argentina"),"usertest");
		plantService.createPlant(new PlantIn("Azul","Argentina"),"usertest");
		plantService.createPlant(new PlantIn("Ayacucho","Argentina"),"usertest");
		plantService.createPlant(new PlantIn("Rawson","Argentina"),"usertest");

		SensorOut s = p1.getSensors().getFirst();
		SensorOut sDisabled = p1.getSensors().get(5);
		List<ReadingIn> readings = new ArrayList<>();
		readings.add(new ReadingIn(s.getSensorOk().getId(),s.getSensorOk().getType().toString(),55));
		readings.add(new ReadingIn(s.getMediumAlert().getId(),s.getMediumAlert().getType().toString(),100));
		readings.add(new ReadingIn(s.getRedAlert().getId(),s.getRedAlert().getType().toString(),12));
		sensorsService.updateSensor(s.getId(),new SensorIn(readings), "usertest");
		//disabled sensor example
		sensorsService.disableEnableSensor(sDisabled.getId(), "usertest");

		};
	}



}
