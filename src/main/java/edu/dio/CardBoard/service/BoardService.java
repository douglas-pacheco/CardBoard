package edu.dio.CardBoard.service;

import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.dao.BoardDAO;
import edu.dio.CardBoard.persistence.entity.BoardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

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
            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();
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

}
