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
    @PostMapping("/create/")
    public String createCard(@RequestBody CardDetailsDTO cardDTO, Model model) throws Exception { // @RequestBody binds JSON/form data to DTO

        CardDetailsDTO returnDTO = cardService.createReturnDTO(cardDTO);
        model.addAttribute("cardDetails", returnDTO); // Add the updated card DTO to the model
        return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to move.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/move-next")
    public String moveCardToNextColumn(@PathVariable Long cardId, Model model) throws Exception {

        CardDetailsDTO returnDTO = cardService.moveToNextColumn(cardId);
        model.addAttribute("cardDetails", returnDTO); // Add the updated card DTO to the model
        return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to block.
     * @param reason DTO containing the reason for blocking.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/block")
    public String blockCard(@PathVariable Long cardId, @RequestParam("reason") final String reason, Model model) throws Exception {

        CardDetailsDTO returnDTO = cardService.block(cardId, reason);
        model.addAttribute("cardDetails", returnDTO); // Add the updated card DTO to the model
        return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to unblock.
     * @param reason DTO containing the reason for unblocking (optional).
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/unblock")
    public String unblockCard(@PathVariable Long cardId, @RequestParam("reason") final String reason, Model model) throws Exception { // reason is optional in request body
        CardDetailsDTO returnDTO = cardService.unblock(cardId, reason);
        model.addAttribute("cardDetails", returnDTO); // Add the updated card DTO to the model
        return "card-details-view";

    }

    /**
     * @param cardId The ID of the card to cancel.
     * @return The name of the Thymeleaf template (`card-details-view`).
     */
    @PostMapping("/{cardId}/cancel")
    public String cancelCard(@PathVariable Long cardId, Model model) throws Exception {
        try {
            CardDetailsDTO returnDTO = cardService.cancel(cardId);
            model.addAttribute("cardDetails", returnDTO); // Add the updated card DTO to the model
        }
        catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage()); // Add error message to model if card cannot be cancelled
        }

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
