package edu.dio.CardBoard.service;


import edu.dio.CardBoard.dto.CardDetailsDTO;
import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.dao.BoardDAO;
import edu.dio.CardBoard.persistence.dao.CardDAO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class CardQueryService {

    private final CardDAO cardDAO;


    @Autowired
    public CardQueryService(CardDAO dao) {
        this.cardDAO = dao;
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        return cardDAO.findById(id);
    }

}
