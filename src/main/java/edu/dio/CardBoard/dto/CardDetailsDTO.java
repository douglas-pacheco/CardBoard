package edu.dio.CardBoard.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
                             String title,
                             String description,
                             Boolean blocked,
                             OffsetDateTime blockedAt,
                             String blockReason,
                             Integer blocksAmount,
                             Long columnId,
                             String columnName
) {
    public CardDetailsDTO(Long id, String title, String description) {
        this(id, title, description, null, null, null, null, null, null);
    }
}
