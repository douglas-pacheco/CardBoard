package edu.dio.CardBoard.dto;


import edu.dio.CardBoard.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind) {
}
