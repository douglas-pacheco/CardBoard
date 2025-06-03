package edu.dio.CardBoard.service;

import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class BoardColumnQueryService {

    private final BoardColumnDAO dao;

    public BoardColumnQueryService(BoardColumnDAO dao) throws SQLException {
        this.dao = dao;

    }

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        return dao.findById(id);
    }

}
