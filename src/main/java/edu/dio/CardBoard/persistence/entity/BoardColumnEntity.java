package edu.dio.CardBoard.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class BoardColumnEntity {

    @Id
    private Long id;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;

    @ManyToOne
    private BoardEntity board ;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude

    @OneToMany
    private List<CardEntity> cards;


}
