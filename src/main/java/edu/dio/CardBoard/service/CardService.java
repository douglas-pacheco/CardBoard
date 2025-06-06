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
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.time.OffsetDateTime;
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


    public CardDetailsDTO createReturnDTO(final CardDetailsDTO request) throws Exception {
        CardEntity entity = create(request);

        CardDetailsDTO cardDTO = new CardDetailsDTO(entity.getId(),
                                                    entity.getTitle(),
                                                    entity.getDescription(),
                                                    null, // blocked
                                                    null, // blockedAt
                                                    null, // blockReason
                                                    0, // blocksAmount
                                                    entity.getBoardColumn().getId(),
                                                    entity.getBoardColumn().getName(),
                                                    entity.getBoardColumn().getBoard().getId()
                                                    );
        return cardDTO;
    }

    public CardDetailsDTO moveToNextColumn(final Long cardId) throws Exception {
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

            CardDetailsDTO updatedCard = new CardDetailsDTO(dto.id(),
                                                            dto.title(),
                                                            dto.description(),
                                                            dto.blocked(),
                                                            dto.blockedAt(),
                                                            dto.blockReason(),
                                                            dto.blocksAmount(),
                                                            nextColumn.id(),
                                                            nextColumn.name(),
                                                            dto.boardId());
            return updatedCard;

        }catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw new Exception("Error while moving card with id %s to next column".formatted(cardId));
        }
    }

    public CardDetailsDTO cancel(final Long cardId) throws Exception{
        try{
            Optional<CardDetailsDTO> optional = cardDAO.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
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

            BoardColumnDTO cancelColumnDTO = boardColumnsInfo.stream()
                    .filter(bc -> bc.kind().equals(CANCEL))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("There is no cancel column in the board"));

            Long cancelColumnId = cancelColumnDTO.id();
            cardDAO.moveToColumn(cancelColumnId, cardId);
            cardDAO.getConnection().commit();

            CardDetailsDTO updatedCard = new CardDetailsDTO(dto.id(),
                                                            dto.title(),
                                                            dto.description(),
                                                            dto.blocked(),
                                                            dto.blockedAt(),
                                                            dto.blockReason(),
                                                            dto.blocksAmount(),
                                                            cancelColumnDTO.id(),
                                                            cancelColumnDTO.name(),
                                                            dto.boardId());
            return updatedCard;
        }catch (SQLException ex){
            cardDAO.getConnection().rollback();
            throw new Exception("Error while canceling card with id %s".formatted(cardId), ex);
        }



    }


    public CardDetailsDTO block(final Long id, final @RequestParam("reason") String reason) throws Exception {
        try{
            Optional<CardDetailsDTO> optional = cardDAO.findById(id);
            CardDetailsDTO cardDetailsDTO = optional.orElseThrow(
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
            OffsetDateTime blockTimeStamp = blockDAO.block(reason, id);
            cardDAO.getConnection().commit();
            blockDAO.getConnection().commit();

            CardDetailsDTO updatedCard = new CardDetailsDTO(cardDetailsDTO.id(),
                    cardDetailsDTO.title(),
                    cardDetailsDTO.description(),
                    true,
                    blockTimeStamp,
                    reason,
                    cardDetailsDTO.blocksAmount()+1,
                    currentColumn.id(),
                    currentColumn.name(),
                    cardDetailsDTO.boardId());
            return updatedCard;

        }catch (SQLException ex) {
            cardDAO.getConnection().rollback();
            blockDAO.getConnection().rollback();
            throw new Exception("Error while blocking card with id %s".formatted(id), ex);
        }
    }

    public CardDetailsDTO unblock(final Long id, final @RequestParam("reason") String reason) throws Exception {
        try{
            Optional<CardDetailsDTO> optional = cardDAO.findById(id);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(id))
            );
            if (!dto.blocked()){
                var message = "Card %s is not blocked".formatted(id);
                throw new CardBlockedException(message);
            }
            blockDAO.unblock(reason, id);
            blockDAO.getConnection().commit();
            cardDAO.getConnection().commit();

            CardDetailsDTO updatedCard = new CardDetailsDTO(dto.id(),
                    dto.title(),
                    dto.description(),
                    false,
                    null, // blockedAt
                    dto.blockReason(),
                    dto.blocksAmount(),
                    dto.columnId(),
                    dto.columnName(),
                    dto.boardId());
            return updatedCard;
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
