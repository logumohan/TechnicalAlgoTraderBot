package com.trading.platform.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.controller.dto.UserAccountDto;
import com.trading.platform.persistence.UserAccountInfoRepository;
import com.trading.platform.persistence.UserAccountRepository;
import com.trading.platform.persistence.entity.UserAccount;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/user-account")
public class UserAccountController {

	private static final Logger LOGGER = LogManager.getLogger(UserAccountController.class);

	@Autowired
	private UserAccountInfoRepository userRepository;

	@Autowired
	private UserAccountRepository repository;

	@PostMapping("/register")
	@LogExecutionTime
	public ResponseEntity<String> registerUser(@RequestBody @NotNull List<UserAccountDto> userAccountList) {
		LOGGER.info("Attempting to register users - {}", userAccountList);
		if (userAccountList.isEmpty()) {
			LOGGER.error("Empty user account list - {}", userAccountList);
			return new ResponseEntity<>("Empty user list!", HttpStatus.BAD_REQUEST);
		}
		userAccountList.stream().map(UserAccountDto::toUserAccount).forEach((UserAccount userAccount) -> {
			userAccount = repository.save(userAccount);
			LOGGER.info("User register successful - {}", userAccount);
		});

		return new ResponseEntity<>("Users added", HttpStatus.OK);
	}

	@PutMapping("/update")
	@LogExecutionTime
	public ResponseEntity<String> updateUser(@RequestBody @NotNull List<UserAccountDto> userAccountList) {
		LOGGER.info("Attempting to update users - {}", userAccountList);
		if (userAccountList.isEmpty()) {
			LOGGER.error("Empty user account list - {}", userAccountList);
			return new ResponseEntity<>("Empty user list!", HttpStatus.BAD_REQUEST);
		}
		userAccountList.stream().map(UserAccountDto::toUserAccount).forEach((UserAccount userAccount) -> {
			userAccount = repository.save(userAccount);
			LOGGER.info("Update user successful - {}", userAccount);
		});

		return new ResponseEntity<>("Users updated", HttpStatus.OK);
	}

	@PutMapping("/remove/{client-id}")
	@LogExecutionTime
	public ResponseEntity<String> removeUser(@PathVariable(name = "client-id", required = true) String clientId) {
		LOGGER.info("Attempting to remove the user for the client Id - {}", clientId);
		repository.deleteById(clientId);
		LOGGER.info("User removed successfully - {}", clientId);

		return new ResponseEntity<>("User Removed", HttpStatus.OK);
	}

	@GetMapping("/master")
	@LogExecutionTime
	public ResponseEntity<UserAccountDto> getMasterAccount() {
		UserAccount userAccount = userRepository.getMasterAccount();

		if (userAccount != null) {
			return new ResponseEntity<>(UserAccountDto.of(userAccount), HttpStatus.OK);
		} else {
			LOGGER.error("No master account is configured!");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/{client-id}")
	@LogExecutionTime
	public ResponseEntity<UserAccountDto> getUserAccountByClientID(
			@PathVariable(name = "client-id", required = true) String clientId) {
		UserAccount userAccount = userRepository.findByClientId(clientId);

		if (userAccount != null) {
			return new ResponseEntity<>(UserAccountDto.of(userAccount), HttpStatus.OK);
		} else {
			LOGGER.error("No user account for the client id - {}", clientId);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/all")
	@LogExecutionTime
	public ResponseEntity<List<UserAccountDto>> getAllUserAccounts() {
		List<UserAccount> userAccountList = userRepository.getAllUserAccount();

		return new ResponseEntity<>(userAccountList.stream().map(UserAccountDto::of).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	@GetMapping("/tradable")
	@LogExecutionTime
	public ResponseEntity<List<UserAccountDto>> getAllTradableUserAccounts() {
		List<UserAccount> userAccountList = userRepository.getAllTradableUserAccount();

		return new ResponseEntity<>(userAccountList.stream().map(UserAccountDto::of).collect(Collectors.toList()),
				HttpStatus.OK);
	}

}
