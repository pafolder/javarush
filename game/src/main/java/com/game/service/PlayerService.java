package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    @Transactional
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional
    public void removePlayerWithId(long id) {
        playerRepository.deleteById(id);
    }
    @Transactional
    public Player getPlayerWithId(long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Transactional
    public void addPlayer(Player player) {
        playerRepository.save(player);
    }
    @Transactional
    public void updatePlayer(Player player) {
        playerRepository.save(player);
    }

}
