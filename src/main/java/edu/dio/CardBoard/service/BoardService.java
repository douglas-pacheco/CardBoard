package edu.dio.CardBoard.service;

import edu.dio.CardBoard.dto.BoardColumnDTO;
import edu.dio.CardBoard.dto.BoardDetailsDTO;
import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.dao.BoardDAO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum;
import edu.dio.CardBoard.persistence.entity.BoardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class BoardService {

    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;


    @Autowired
    public BoardService(BoardDAO dao, BoardColumnDAO boardColumnDAO) {
        this.boardDAO = dao;
        this.boardColumnDAO = boardColumnDAO;
    }

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        try{
            boardDAO.insert(entity);
            var columns = entity.getBoardColumns().stream().peek(c -> c.setBoard(entity)).toList();
            for (var column :  columns){
                boardColumnDAO.insert(column);
            }
            boardColumnDAO.getConnection().commit();
        } catch (SQLException e) {
            boardColumnDAO.getConnection().rollback();
            throw e;
        }
        return entity;
    }

    public boolean delete(final Long id) throws SQLException {
        try{
            if (!boardDAO.exists(id)) {
                return false;
            }
            boardDAO.delete(id);
            boardDAO.getConnection().commit();
            return true;
        } catch (SQLException e) {
            boardDAO.getConnection().rollback();
            throw e;
        }
    }


//    public BoardColumnDTO findInitialColumn(BoardDetailsDTO boardDTO) throws Exception {
//        return getBoardColumnDTOS(boardDTO).stream()
//                .filter(bc -> bc.kind().equals(BoardColumnKindEnum.INITIAL))
//                .findFirst()
//                .orElseThrow(() -> new Exception("Initial column not found for board with id: " + boardDTO.id()));
//    }


    public List<BoardColumnDTO> getBoardColumnDTOS(BoardDetailsDTO boardDetails) {
        List<BoardColumnDTO> boardColumnsInfo;
        boardColumnsInfo = boardDetails.columns().stream()
                .map(bc -> new BoardColumnDTO(bc.id(), bc.name(), bc.order(), bc.kind()))
                .toList();
        return boardColumnsInfo;
    }

//    public BoardColumnDTO defineInitialColumn(BoardDetailsDTO boardDTO) throws Exception {
//        BoardColumnDTO initialColumn;
//        initialColumn = boardQueryService.findColumnsByBoardId(boardDTO.id())
//                .stream()
//                .filter(bc -> bc.kind().equals(BoardColumnKindEnum.INITIAL))
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("No initial column found for the board"));
//        return initialColumn;
//    }

    public BoardColumnEntity defineInitialColumn(List<BoardColumnEntity> columns) {
        return columns.stream()
                .filter(column -> column.getKind().equals(BoardColumnKindEnum.INITIAL))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No initial column found for the board"));
    }


    public BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        BoardColumnEntity boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setColumnorder(order);
        return boardColumn;
    }
}
