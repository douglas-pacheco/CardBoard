package edu.dio.CardBoard.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class CardEntity {

    @Id
    private Long id;
    private String title;
    private String description;
    @ManyToOne
    private BoardColumnEntity boardColumn ;


}
