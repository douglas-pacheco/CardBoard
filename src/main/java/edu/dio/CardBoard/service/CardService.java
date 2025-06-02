package edu.dio.CardBoard.service;

import edu.dio.CardBoard.dto.BoardColumnInfoDTO;
import edu.dio.CardBoard.exception.CardBlockedException;
import edu.dio.CardBoard.exception.CardFinishedException;
import edu.dio.CardBoard.exception.EntityNotFoundException;
import edu.dio.CardBoard.persistence.dao.BlockDAO;
import edu.dio.CardBoard.persistence.dao.CardDAO;
import edu.dio.CardBoard.persistence.entity.CardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

import static edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum.CANCEL;
import static edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum.FINAL;


@Component
public class CardService {

    private final CardDAO cardDAO;
    private final BlockDAO blockDAO;

    @Autowired
    public CardService(CardDAO cardDAO, BlockDAO blockDAO) {
        this.cardDAO = cardDAO;
        this.blockDAO = blockDAO;
    }

    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            cardDAO.insert(entity);
            cardDAO.getConnection().commit();
            return entity;
        } catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        try{
            var optional = cardDAO.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()){
                var message = "O card %s está bloqueado, é necesário desbloquea-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card já foi finalizado");
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
            cardDAO.moveToColumn(nextColumn.id(), cardId);
            cardDAO.getConnection().commit();
        }catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId ,
                       final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        try{
            var optional = cardDAO.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            if (dto.blocked()){
                var message = "O card %s está bloqueado, é necesário desbloquea-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card já foi finalizado");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
            cardDAO.moveToColumn(cancelColumnId, cardId);
            cardDAO.getConnection().commit();
        }catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var optional = cardDAO.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );
            if (dto.blocked()){
                var message = "O card %s já está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                var message = "O card está em uma coluna do tipo %s e não pode ser bloqueado"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            blockDAO.block(reason, id);
            cardDAO.getConnection().commit();
            blockDAO.getConnection().commit();
        }catch (SQLException ex) {
            cardDAO.getConnection().commit();
            blockDAO.getConnection().commit();
            throw ex;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try{
            var optional = cardDAO.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );
            if (!dto.blocked()){
                var message = "O card %s não está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            blockDAO.unblock(reason, id);
            blockDAO.getConnection().commit();
            cardDAO.getConnection().commit();

        }catch (SQLException ex) {
            blockDAO.getConnection().rollback();
            cardDAO.getConnection().rollback();
            throw ex;
        }
    }

}
