package edu.dio.CardBoard.service;

import edu.dio.CardBoard.dto.BoardDetailsDTO;
import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.dao.BoardDAO;
import edu.dio.CardBoard.persistence.entity.BoardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public class BoardQueryService {

    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;


    @Autowired
    public BoardQueryService(BoardDAO dao, BoardColumnDAO boardColumnDAO) {
        this.boardDAO = dao;
        this.boardColumnDAO = boardColumnDAO;
    }


    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var optional = boardDAO.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        var optional = boardDAO.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

}
