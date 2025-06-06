package edu.dio.CardBoard.controller;

import edu.dio.CardBoard.dto.BoardDetailsDTO;
import edu.dio.CardBoard.dto.CardDetailsDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/board-menu")
public class BoardMenuController {




    @GetMapping
    public String showBoardMenu(Model model) {
//        Long forwardedBoardId = (Long) model.getAttribute("selectedBoardId");
//        String forwardedBoardName = (String) model.getAttribute("selectedBoardName");
        BoardDetailsDTO forwardedBoard = (BoardDetailsDTO) model.getAttribute("selectedBoard");
        Collection<List<CardDetailsDTO>> cardsByColumn = forwardedBoard.cards().values();

        // Calculate maxCardCount for the table rows in the template
        int maxCardCount;
        maxCardCount = cardsByColumn.stream()
                .filter(Objects::nonNull)
                .mapToInt(List::size)
                .max().orElse(1);
        model.addAttribute("maxCardCount", maxCardCount > 0 ? maxCardCount : 1);


        return "board-menu";
    }
}
