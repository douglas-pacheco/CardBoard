package edu.dio.CardBoard.controller;



import edu.dio.CardBoard.dto.CardDetailsDTO;
import edu.dio.CardBoard.service.CardQueryService;
import edu.dio.CardBoard.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/cards")
public class CardController {


    private final CardService cardService;
    private final CardQueryService cardQueryService;

    @Autowired
    public CardController(CardService cardService, CardQueryService cardQueryService) {
        this.cardService = cardService;
        this.cardQueryService = cardQueryService;
    }


    /**
     * @param cardDTO DTO containing the card's title and description.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping
    public String createCard(@RequestBody CardDetailsDTO cardDTO) throws Exception { // @RequestBody binds JSON/form data to DTO

        cardService.create(cardDTO);
        return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to move.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/move-next")
    public String moveCardToNextColumn(@PathVariable Long cardId) throws Exception {

            cardService.moveToNextColumn(cardId);
            return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to block.
     * @param cardDTO DTO containing the reason for blocking.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/block")
    public String blockCard(@PathVariable Long cardId, @RequestBody CardDetailsDTO cardDTO) throws Exception {



            cardService.block(cardId, cardDTO.blockReason());
            return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to unblock.
     * @param cardDTO DTO containing the reason for unblocking (optional).
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/unblock")
    public String unblockCard(@PathVariable Long cardId, @RequestBody CardDetailsDTO cardDTO) throws Exception { // reason is optional in request body
            String reason = (cardDTO != null) ? cardDTO.blockReason() : null;
            cardService.unblock(cardId, reason);
            return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to cancel.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/cancel")
    public String cancelCard(@PathVariable Long cardId) throws Exception {

            cardService.cancel(cardId);
            return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to retrieve.
     * @param model The Spring Model to pass data to the view.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @GetMapping("/{cardId}/view")
    public String showCard(@PathVariable Long cardId, Model model) throws Exception {

            cardQueryService.findById(cardId)
                    .ifPresentOrElse(
                            card -> model.addAttribute("cardDetails", card), // Add card DTO to model if found
                            () -> model.addAttribute("message", "There is no card with ID " + cardId) // Message if not found
                    );
            return "card-details-view";

    }


}
