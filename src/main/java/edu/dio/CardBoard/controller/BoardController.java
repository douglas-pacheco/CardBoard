package edu.dio.CardBoard.controller;


import edu.dio.CardBoard.dto.BoardCreationDTO;
import edu.dio.CardBoard.dto.BoardDetailsDTO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import edu.dio.CardBoard.persistence.entity.BoardEntity;
import edu.dio.CardBoard.service.BoardQueryService;
import edu.dio.CardBoard.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum.*;

@Controller
@RequestMapping("/boards") // Defines a prefix for all routes in this controller
public class BoardController {


    private final BoardService boardService;
    private final BoardQueryService boardQueryService;

    @Autowired
    public BoardController(BoardService boardService, BoardQueryService boardQueryService) {
        this.boardService = boardService;
        this.boardQueryService = boardQueryService;
    }


    @GetMapping("/create") // Accessible at http://localhost:8080/boards/create
    public String createBoard(Model model) {
        model.addAttribute("boardCreationDTO", new BoardCreationDTO());
        return "create-board";
    }

    /**
     * Handles the form submission to create a new board.
     * Accessible via POST request to /boards/save.
     * Validates the submitted data and calls the BoardService to insert the board.
     */
    @PostMapping("/save")
    public String saveBoard(@Valid @ModelAttribute("boardCreationDTO") BoardCreationDTO boardCreationDTO,
                            BindingResult bindingResult, // Capture validation results
                            RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // If there are errors, return to the form view to display them
            return "create-board";
        }

        try {

            BoardEntity boardEntity = new BoardEntity();
            boardEntity.setName(boardCreationDTO.getBoardName());

            List<BoardColumnEntity> columns = new ArrayList<>();
            int currentOrder = 0; // Keep track of column order

            // Create Initial Column
            BoardColumnEntity initialColumn = boardService.createColumn(boardCreationDTO.getInitialColumnName(), INITIAL, currentOrder++);
            columns.add(initialColumn);

            // Create Additional Pending Columns from the list
            for (String pendingColumnName : boardCreationDTO.getPendingColumnNames()) {
                BoardColumnEntity pendingColumn = boardService.createColumn(pendingColumnName, PENDING, currentOrder++);
                columns.add(pendingColumn);
            }

            // Create Final Column
            BoardColumnEntity finalColumn = boardService.createColumn(boardCreationDTO.getFinalColumnName(), FINAL, currentOrder);
            columns.add(finalColumn);

            // Create Cancellation Column
            BoardColumnEntity cancelColumn = boardService.createColumn(boardCreationDTO.getCancelColumnName(), CANCEL, currentOrder);
            columns.add(cancelColumn);

            boardEntity.setBoardColumns(columns);

            boardService.insert(boardEntity);
            redirectAttributes.addFlashAttribute("successMessage", "Board '" + boardCreationDTO.getBoardName() + "' created successfully!");

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating board: " + ex.getMessage());
            return "redirect:/boards/create";
        }

        return "redirect:/mainmenu"; // Redirect to the main menu after successful creation
    }


    @GetMapping("/select")
    public String findBoard(@RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes) throws Exception {
        try {
            Optional<BoardDetailsDTO> optionalBoard = boardQueryService.findByIdFetchingRelationships(boardId);

            if (optionalBoard.isPresent()) {
                BoardDetailsDTO foundBoard = optionalBoard.get();
                redirectAttributes.addFlashAttribute("selectedBoardId", foundBoard.id());
                redirectAttributes.addFlashAttribute("selectedBoardName", foundBoard.name());
                redirectAttributes.addFlashAttribute("selectedBoard", foundBoard); // Can pass the full object
                return "redirect:/board-menu";
            } else {
                // If board is not found, redirect to a specific view with an error message
                redirectAttributes.addFlashAttribute("errorMessage", "No board with ID " + boardId + " was found.");
                return "board-not-found"; // Redirect to the 'board not found' page
            }

        }
        catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Database error while trying to find board: " + ex.getMessage());
            return "board-not-found"; // Redirect to the 'board not found' page on DB error
        }
    }


    @GetMapping("/delete")
    public String showDeleteBoardForm() {
        return "delete-board"; // Returns the template name located at src/main/resources/templates/boards/delete-board.html
    }

    @PostMapping("/perform-delete")
    public String performDeleteBoard(@RequestParam("boardId") Long boardId, // Get boardId from form input
                                     RedirectAttributes redirectAttributes) {

        try  { // Assuming getConnection() is how you get a DB connection
            if (boardService.delete(boardId)) {
                redirectAttributes.addFlashAttribute("successMessage", "Board with ID " + boardId + " was successfully deleted.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No board with ID " + boardId + " was found or could be deleted.");
            }

        } catch (SQLException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Database error during board deletion: " + ex.getMessage());
        } catch (RuntimeException ex) {
            // Catch any runtime exceptions from your service methods
            redirectAttributes.addFlashAttribute("errorMessage", "Application error during board deletion: " + ex.getMessage());
        }

        return "redirect:/mainmenu"; // Redirect back to the main menu with a message
    }


}
