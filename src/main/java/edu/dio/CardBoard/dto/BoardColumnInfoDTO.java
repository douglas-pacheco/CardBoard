package edu.dio.CardBoard.dto;


import edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}
