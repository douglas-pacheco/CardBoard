package edu.dio.CardBoard.dto;

import java.util.List;
import java.util.Map;

public record BoardDetailsDTO(Long id,
                              String name,
                              List<BoardColumnDTO> columns,
                              Map<Long, List<CardDetailsDTO>> cards) {
}
