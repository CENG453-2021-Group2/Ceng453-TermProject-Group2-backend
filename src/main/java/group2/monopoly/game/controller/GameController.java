package group2.monopoly.game.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.game.entity.Game;
import group2.monopoly.game.entity.GameTableConfiguration;
import group2.monopoly.game.entity.Player;
import group2.monopoly.game.exception.GameManagementException;
import group2.monopoly.game.exception.GameOverException;
import group2.monopoly.game.exception.GameFaultyMoveException;
import group2.monopoly.game.service.*;
import group2.monopoly.mapper.ObjectMapperSingleton;
import group2.monopoly.security.jwt.JwtHelper;
import group2.monopoly.validation.CustomGroupSequence;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * It contains two endpoints, one for login and one for registration
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/game", produces = "application/json", consumes = "application/json")
public class GameController {

    private final JwtHelper jwtHelper;

    private final DiceGeneratorService diceGeneratorService;

    private final AuthenticationManager authenticationManager;

    private final GameCellPriceService gameCellPriceService;

    private final GameEngineService gameEngineService;

    private final GameManagerService gameManagerService;

    private final GameScoreService gameScoreService;

    private final ObjectMapper objectMapper = ObjectMapperSingleton.getMapper();

    @Autowired
    public GameController(DiceGeneratorService diceGeneratorService,
                          GameCellPriceService gameCellPriceService,
                          GameEngineService gameEngineService,
                          GameManagerService gameManagerService,
                          GameScoreService gameScoreService,
                          JwtHelper jwtHelper,
                          AuthenticationManager authenticationManager) {
        this.diceGeneratorService = diceGeneratorService;
        this.gameCellPriceService = gameCellPriceService;
        this.gameEngineService = gameEngineService;
        this.gameManagerService = gameManagerService;
        this.gameScoreService = gameScoreService;
        this.jwtHelper = jwtHelper;
        this.authenticationManager = authenticationManager;
    }

    /**
     * It returns a random dice number
     *
     * @return A generic response with a message of two integers
     */
    @GetMapping("/dice")
    public ResponseEntity<JsonNode> throwDice() {
        ;
    }




}
