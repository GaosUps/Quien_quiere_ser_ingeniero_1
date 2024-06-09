package io.github.gaosups.qqi.controller;

import io.github.gaosups.qqi.model.Player;
import io.github.gaosups.qqi.model.dto.PlayerDTO;
import io.github.gaosups.qqi.model.exceptions.PlayerNotFoundException;
import io.github.gaosups.qqi.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerController {

	private final PlayerService playerService;
	private final MessageSource messageSource;

	@GetMapping
	public ResponseEntity<Map<String, List<PlayerDTO>>> getPlayers() {

		return ResponseEntity.status(HttpStatus.OK)
			       .body(Map.of("players", playerService.findAll()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PlayerDTO> getPlayerById(
		@PathVariable UUID id
	) {
		PlayerDTO playerDTO =
			playerService.findById(id)
				.orElseThrow(
					() -> new PlayerNotFoundException(
						messageSource.getMessage(
							"NotFound.player.message",
							new String[] { id.toString() },
							Locale.US)
					));

		return ResponseEntity.status(HttpStatus.OK)
			       .body(playerDTO);
	}

	@PostMapping
	public ResponseEntity<Object> createPlayer(

		@Valid @RequestBody Player player,
		BindingResult result
	) {
		Map<String, String> errors = new HashMap<>();

		if (result.hasErrors()) {

			result.getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

			return ResponseEntity.badRequest()
				       .body(errors);
		}

		return ResponseEntity.status(CREATED)
			       .body(playerService.save(player));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updatePlayerById(
		@PathVariable UUID id,
		@Valid @RequestBody Player player,
		@NotNull BindingResult result
	) {
		Map<String, String> errors = new HashMap<>();

		if (result.hasErrors()) {
			result.getFieldErrors()
				.forEach(
					error -> errors.put(error.getField(), error.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				       .body(errors);
		}

		playerService.updateUserById(id, player);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			       .build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletePlayerById(
		@PathVariable UUID id
	) {
		playerService.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			       .build();
	}
}
