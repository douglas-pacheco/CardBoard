package edu.dio.CardBoard.service;

import edu.dio.CardBoard.dto.BoardColumnDTO;
import edu.dio.CardBoard.dto.BoardDetailsDTO;
import edu.dio.CardBoard.dto.CardDetailsDTO;
import edu.dio.CardBoard.exception.CardBlockedException;
import edu.dio.CardBoard.exception.CardFinishedException;
import edu.dio.CardBoard.exception.EntityNotFoundException;
import edu.dio.CardBoard.persistence.dao.BlockDAO;
import edu.dio.CardBoard.persistence.dao.CardDAO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import edu.dio.CardBoard.persistence.entity.CardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum.CANCEL;
import static edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum.FINAL;


@Component
public class CardService {

    private final CardDAO cardDAO;
    private final BlockDAO blockDAO;
    private final BoardQueryService boardQueryService;
    private final BoardColumnQueryService boardColumnQueryService;
    private final BoardService boardService;

    @Autowired
    public CardService(CardDAO cardDAO, BlockDAO blockDAO, BoardQueryService boardQueryService, BoardColumnQueryService boardColumnQueryService, BoardService boardService) {
        this.cardDAO = cardDAO;
        this.blockDAO = blockDAO;
        this.boardQueryService = boardQueryService;
        this.boardColumnQueryService = boardColumnQueryService;
        this.boardService = boardService;
    }

    public CardEntity create(final CardDetailsDTO request) throws Exception {
        try {

            List<BoardColumnEntity> columns= boardColumnQueryService.findColumnsByBoardId(request.boardId());
            BoardColumnEntity targetColumn = boardService.defineInitialColumn(columns);

            CardEntity card = new CardEntity();
            card.setTitle(request.title());
            card.setDescription(request.description());
            card.setBoardColumn(targetColumn);

            cardDAO.insert(card);
            cardDAO.getConnection().commit();
            return card;
        } catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw new Exception("Error while creating card ");
        }
    }

    public void moveToNextColumn(final Long cardId) throws Exception {
        try{
            Optional<CardDetailsDTO> optional = cardDAO.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(cardId))
            );
            if (dto.blocked()){
                var message = "Card %s is blocked, to move it is necessary to unblock it".formatted(cardId);
                throw new CardBlockedException(message);
            }
            Long dtoBoardId = dto.boardId();
            Optional<BoardDetailsDTO> boardDTO = boardQueryService.findBoardByIdFetchingBlocks(dtoBoardId);
            if (boardDTO.isEmpty()) {
                throw new EntityNotFoundException("Board not found for this Card");
            }
            List<BoardColumnDTO> boardColumnsInfo = boardService.getBoardColumnDTOS(boardDTO.get());

            BoardColumnDTO currentColumn = getCurrentBoardColumnDTO(boardColumnsInfo, dto);

            checkForFinishedCard(currentColumn);

            BoardColumnDTO nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("This board has no next column"));

            cardDAO.moveToColumn(nextColumn.id(), cardId);
            cardDAO.getConnection().commit();
        }catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw new Exception("Error while moving card with id %s to next column".formatted(cardId));
        }
    }

    public void cancel(final Long cardId) throws Exception{
        try{
            var optional = cardDAO.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(cardId))
            );
            if (dto.blocked()){
                var message = "Card %s is blocked, to move it is necessary to unblock it".formatted(cardId);
                throw new CardBlockedException(message);
            }

            Optional<BoardDetailsDTO> boardDetails = boardQueryService.findBoardByIdFetchingBlocks(dto.boardId());
            if( boardDetails.isEmpty() ){
                throw new EntityNotFoundException("Board not found for this card");
            }
            List<BoardColumnDTO> boardColumnsInfo = boardService.getBoardColumnDTOS(boardDetails.get());

            BoardColumnDTO currentColumn = getCurrentBoardColumnDTO(boardColumnsInfo, dto);

            checkForFinishedCard(currentColumn);

            Long cancelColumnId = boardColumnsInfo.stream()
                    .filter(bc -> bc.kind().equals(CANCEL))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("There is no cancel column in the board"))
                    .id();
            cardDAO.moveToColumn(cancelColumnId, cardId);
            cardDAO.getConnection().commit();
        }catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw new Exception("Error while canceling card with id %s".formatted(cardId), ex);
        }
    }


    public void block(final Long id, final String reason) throws Exception {
        try{
            var optional = cardDAO.findById(id);
            var cardDetailsDTO = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(id))
            );
            if (cardDetailsDTO.blocked()){
                var message = "Card %s is already blocked".formatted(id);
                throw new CardBlockedException(message);
            }

            List<BoardColumnDTO> boardColumnDTOS = boardQueryService.findColumnsByBoardId(cardDetailsDTO.boardId());
            BoardColumnDTO currentColumn = this.getCurrentBoardColumnDTO(boardColumnDTOS,cardDetailsDTO);

            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                var message = "Card is in a column of %s type and cannot be blocked"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            blockDAO.block(reason, id);
            cardDAO.getConnection().commit();
            blockDAO.getConnection().commit();
        }catch (SQLException ex) {
            cardDAO.getConnection().rollback();
            blockDAO.getConnection().rollback();
            throw new Exception("Error while blocking card with id %s".formatted(id), ex);
        }
    }

    public void unblock(final Long id, final String reason) throws Exception {
        try{
            var optional = cardDAO.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(id))
            );
            if (!dto.blocked()){
                var message = "Card %s is not blocked".formatted(id);
                throw new CardBlockedException(message);
            }
            blockDAO.unblock(reason, id);
            blockDAO.getConnection().commit();
            cardDAO.getConnection().commit();

        }catch (SQLException ex) {
            blockDAO.getConnection().rollback();
            cardDAO.getConnection().rollback();
            throw new Exception("Error while unblocking card with id %s".formatted(id), ex);
        }
    }



    private BoardColumnDTO getCurrentBoardColumnDTO(List<BoardColumnDTO> boardColumnsInfo, CardDetailsDTO dto) {
        BoardColumnDTO currentColumn;
        currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Inconsistency in Card's column information"));
        return currentColumn;
    }

//    private BoardColumnEntity getCurrentColumn(CardDetailsDTO dto) throws Exception {
//        BoardColumnEntity currentColumn;
//        currentColumn = boardColumnQueryService.findById(dto.columnId())
//                .orElseThrow(() -> new EntityNotFoundException("Column with id %s was not found".formatted(dto.columnId())));
//        return currentColumn;
//    }

    private void checkForFinishedCard(BoardColumnDTO currentColumn) {
        if (currentColumn.kind().equals(FINAL)){
            throw new CardFinishedException("Card is already finished");
        }
    }



}
