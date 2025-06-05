package edu.dio.CardBoard.service;

import edu.dio.CardBoard.persistence.dao.BoardColumnDAO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BoardColumnQueryService {

    private final BoardColumnDAO dao;

    public BoardColumnQueryService(BoardColumnDAO dao)  {
        this.dao = dao;
    }

    public List<BoardColumnEntity> findColumnsByBoardId(final Long id) throws Exception {
        return dao.findBoardColumnsByBoardId(id);
    }



//    public Optional<BoardColumnEntity> findById(final Long id) throws Exception {
//        return dao.findById(id);
//    }
}
