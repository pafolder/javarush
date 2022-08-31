package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.game.controller.PlayerOrder.*;

@RestController
    public class RpgRestController {
        private HttpHeaders responseHeaders = new HttpHeaders();
        private PlayerService playerService;

        @Autowired
        public RpgRestController(PlayerService playerService) {
            this.playerService = playerService;
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        @PostMapping(value = "/rest/players")
        public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
            if (player == null || !player.isValid()) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
            }
            player.computeLevelAndUntilNextLevel();
            if (player.isBanned() == null)
                player.setBanned(false);
            playerService.addPlayer(player);
            return new ResponseEntity<>(player, responseHeaders, HttpStatus.OK);
        }

        @PostMapping( "/rest/players/{id}")
        public ResponseEntity<Player> updatePlayer(@RequestBody Player playerUpdate, @PathVariable String id) {
            long iD;
            try {
                iD = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                iD = -1;
            }
            if (iD <= 0) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
            }
            if (iD > 0) {
                playerUpdate.setId(iD);
            }

            if (playerUpdate == null) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            if (playerUpdate.isEmpty()) {
                Player playerWithId = playerService.getPlayerWithId(iD);
                return new ResponseEntity<>(playerWithId, responseHeaders, HttpStatus.OK);
            }

            if(!playerUpdate.areNonNullValid())
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);

            if (playerService.getPlayerWithId(iD) == null)
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);

            Player player = playerService.getPlayerWithId(iD);

            if (playerUpdate.getName() == null)
                playerUpdate.setName(player.getName());
            if (playerUpdate.getTitle() == null)
                playerUpdate.setTitle(player.getTitle());
            if (playerUpdate.getBirthday() == null)
                playerUpdate.setBirthday(player.getBirthday());
            if (playerUpdate.getExperience() == null)
                playerUpdate.setExperience(player.getExperience());
            if (playerUpdate.getRace() == null)
                playerUpdate.setRace(player.getRace());
            if (playerUpdate.getProfession() == null)
                playerUpdate.setProfession(player.getProfession());
            if (playerUpdate.isBanned() == null)
                playerUpdate.setBanned(player.isBanned());

            playerUpdate.computeLevelAndUntilNextLevel();

            if (!(playerUpdate.isValid() && playerUpdate.getId() > 0)) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST  );
            }

            playerService.updatePlayer(playerUpdate);

            return new ResponseEntity<>(playerUpdate, responseHeaders, HttpStatus.OK);
        }

        @DeleteMapping(value = "/rest/players/{id}")
        public ResponseEntity<String> deletePlayer(@PathVariable String id) {
            long iD = 0;
            try {
                iD = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                iD = -1;
            }
            if (iD <= 0)
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);

            if (playerService.getPlayerWithId(iD) != null) {
                playerService.removePlayerWithId(iD);
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
            }
            else
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
        }


        @GetMapping(value = "/rest/players/count")
        public ResponseEntity<Integer> getPlayerCount(
                @RequestParam(value = "name", required = false) String name,
                @RequestParam (value = "title", required = false) String title,
                @RequestParam (value = "race", required = false) Race race,
                @RequestParam (value = "profession", required = false) Profession profession,
                @RequestParam (value = "after", required = false) Long after,
                @RequestParam (value = "before", required = false) Long before,
                @RequestParam (value = "banned", required = false) Boolean banned,
                @RequestParam (value = "minExperience", required = false) Integer minExperience,
                @RequestParam (value = "maxExperience", required = false) Integer maxExperience,
                @RequestParam (value = "minLevel", required = false) Integer minLevel,
                @RequestParam (value = "maxLevel", required = false) Integer maxLevel) {
            Integer count = 0;
            List<Player> result = playerService.getAllPlayers();

            if (banned != null)
                result = result.stream().filter(player -> (player.isBanned().equals(banned)))
                        .collect(Collectors.toList());

            if (minLevel != null)
                result = result.stream().filter(player -> (player.getLevel() >= minLevel))
                        .collect(Collectors.toList());

            if (maxLevel != null) {
                result = result.stream().filter(player -> (player.getLevel() <= maxLevel))
                        .collect(Collectors.toList());
            }

            if (minExperience != null)
                result = result.stream().filter(player -> (player.getExperience() >= minExperience))
                        .collect(Collectors.toList());

            if (maxExperience != null)
                result = result.stream().filter(player -> (player.getExperience() <= maxExperience))
                        .collect(Collectors.toList());

            if (name != null)
                result = result.stream().filter(player -> player.getName().contains(name))
                        .collect(Collectors.toList());

            if (title != null)
                result = result.stream().filter(player -> player.getTitle().contains(title))
                        .collect(Collectors.toList());

            if (after != null)
                result = result.stream().filter(player -> player.getBirthday().getTime() >= after)
                        .collect(Collectors.toList());

            if (before != null)
                result = result.stream().filter(player -> player.getBirthday().getTime() <= before)
                        .collect(Collectors.toList());

            if (race != null)
                result = result.stream().filter(player -> player.getRace() == race)
                        .collect(Collectors.toList());

            if (profession != null)
                result = result.stream().filter(player -> player.getProfession() == profession)
                        .collect(Collectors.toList());
            count = result.size();
            return new ResponseEntity<>(count, responseHeaders, HttpStatus.OK);
        }

        @GetMapping(value = "/rest/players")
        public ResponseEntity<List<Player>> getAllPlayers(
                @RequestParam (value = "name", required = false) String name,
                @RequestParam (value = "title", required = false) String title,
                @RequestParam (value = "race", required = false) Race race,
                @RequestParam (value = "profession", required = false) Profession profession,
                @RequestParam (value = "after", required = false) Long after,
                @RequestParam (value = "before", required = false) Long before,
                @RequestParam (value = "banned", required = false) Boolean banned,
                @RequestParam (value = "minExperience", required = false) Integer minExperience,
                @RequestParam (value = "maxExperience", required = false) Integer maxExperience,
                @RequestParam (value = "minLevel", required = false) Integer minLevel,
                @RequestParam (value = "maxLevel", required = false) Integer maxLevel,
                @RequestParam (value = "order", required = false) PlayerOrder order,
                @RequestParam (value = "pageNumber", required = false) Integer pageNumber,
                @RequestParam (value = "pageSize", required = false) Integer pageSize) {

            List<Player> result = playerService.getAllPlayers();

            if (banned != null) {
                result = result.stream().filter(player -> (player.isBanned().equals(banned)))
                        .collect(Collectors.toList());
            }
            if (minLevel != null) {
                result = result.stream().filter(player -> (player.getLevel() >= minLevel))
                        .collect(Collectors.toList());
            }
            if (maxLevel != null) {
                result = result.stream().filter(player -> (player.getLevel() <= maxLevel))
                        .collect(Collectors.toList());
            }
            if (minExperience != null) {
                result = result.stream().filter(player -> (player.getExperience() >= minExperience))
                        .collect(Collectors.toList());
            }
            if (maxExperience != null) {
                result = result.stream().filter(player -> (player.getExperience() <= maxExperience))
                        .collect(Collectors.toList());
            }
            if (name != null) {
                result = result.stream().filter(player -> player.getName().contains(name))
                        .collect(Collectors.toList());
            }
            if (title != null) {
                result = result.stream().filter(player -> player.getTitle().contains(title))
                        .collect(Collectors.toList());
            }
            if (after != null) {
                result = result.stream().filter(player -> player.getBirthday().getTime() >= after)
                        .collect(Collectors.toList());
            }
            if (before != null) {
                result = result.stream().filter(player -> player.getBirthday().getTime() <= before)
                        .collect(Collectors.toList());
            }
            if (race != null) {
                result = result.stream().filter(player -> player.getRace() == race)
                        .collect(Collectors.toList());
            }
            if (profession != null) {
                result = result.stream().filter(player -> player.getProfession() == profession)
                        .collect(Collectors.toList());
            }
             if (order == null) {
                 order = ID;
             }
             switch (order) {
                 case ID:
                result = result.stream().sorted(Comparator.comparing(Player::getId)).collect(Collectors.toList());
                break;
                 case BIRTHDAY:
                result = result.stream().sorted(Comparator.comparing(Player::getBirthday)).collect(Collectors.toList());
                break;
                 case EXPERIENCE:
                result = result.stream().sorted(Comparator.comparing(Player::getExperience)).collect(Collectors.toList());
                break;
                 case LEVEL:
                result = result.stream().sorted(Comparator.comparing(Player::getLevel)).collect(Collectors.toList());
                break;
                 case  NAME:
                result = result.stream().sorted(Comparator.comparing(Player::getName)).collect(Collectors.toList());
                break; }
            if (pageNumber == null) {
                pageNumber = 0;
            }
            if (pageSize == null) {
                pageSize = 3;
            }
            int offset = pageNumber * pageSize;
            List<Player> pagedResult = new ArrayList<>();
            for (int i = offset; i < Math.min(offset + pageSize, result.size()); i++) {
                pagedResult.add(result.get(i));
            }
            return new ResponseEntity<>(pagedResult, responseHeaders, HttpStatus.OK);
        }


        @GetMapping("/rest/players/{id}")
        public ResponseEntity<Player> getPlayer(@PathVariable String id, Model model) {
            int iD = 0;
            try {
                iD = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            if ( iD == 0) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
            }
            Player player;
            if ( (player = playerService.getPlayerWithId(iD)) == null) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
            }

            if (!player.isValid()) {
                return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(playerService.getPlayerWithId(iD), responseHeaders, HttpStatus.OK);
        }
 }

