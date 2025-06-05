package edu.dio.CardBoard.service;

import edu.dio.CardBoard.dto.BoardColumnDTO;
import edu.dio.CardBoard.dto.BoardDetailsDTO;
import edu.dio.CardBoard.dto.CardDetailsDTO;
import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.dao.BoardDAO;
import edu.dio.CardBoard.persistence.dao.CardDAO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import edu.dio.CardBoard.persistence.entity.BoardEntity;
import edu.dio.CardBoard.persistence.entity.CardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class BoardQueryService {

    private final BoardDAO boardDAO;
    private final CardDAO cardDAO;
    private final BoardColumnDAO boardColumnDAO;


    @Autowired
    public BoardQueryService(BoardDAO dao, CardDAO cardDAO, BoardColumnDAO boardColumnDAO) {
        this.boardDAO = dao;
        this.cardDAO = cardDAO;
        this.boardColumnDAO = boardColumnDAO;
    }


    public Optional<BoardDetailsDTO> findByIdFetchingRelationships(final Long id) throws Exception {
        Optional<BoardEntity> optional;
        Optional<BoardDetailsDTO> optionalDTO;
        try {
            optional = boardDAO.findById(id);
            if (optional.isPresent()){
                var entity = optional.get();
                List<BoardColumnEntity> columnsByBoardId = boardColumnDAO.findByBoardId(entity.getId());
                List<BoardColumnDTO> columnsDTO = columnsByBoardId
                        .stream().map(
                        column -> new BoardColumnDTO(column.getId(), column.getName(), column.getOrder(), column.getKind()))
                        .toList();
                BoardDetailsDTO boardDetailsDTO = new BoardDetailsDTO(entity.getId(), entity.getName(), columnsDTO, new HashMap<>());
                optionalDTO = Optional.of(boardDetailsDTO);
                for (BoardColumnEntity column : columnsByBoardId) {
                    List<CardEntity> cards = cardDAO.findAllByColumn(column);
                    List<CardDetailsDTO> cardDetails = cards.stream()
                                .map(card -> new CardDetailsDTO(card.getId(), card.getTitle(), card.getDescription(), id))
                                .toList();
                    boardDetailsDTO.cards().put(column.getId(), cardDetails);
                }
                return optionalDTO;
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public Optional<BoardDetailsDTO> findBoardByIdFetchingBlocks(final Long id) throws Exception {
        var optional = boardDAO.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns, null);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public List<BoardColumnDTO> findColumnsByBoardId(Long id) throws Exception {

        try {
            List<BoardColumnDTO> columns;
            columns = boardColumnDAO.findByBoardIdWithDetails(id);
            return columns;
        } catch (SQLException e) {
            throw new Exception("Error fetching columns for board with id: " + id, e);
        }

    }
}
