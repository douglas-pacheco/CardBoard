CREATE TABLE board_column_entity
(
    id       BIGINT NOT NULL,
    name     VARCHAR(255),
    "order"  INT    NOT NULL,
    kind     SMALLINT,
    board_id BIGINT,
    CONSTRAINT pk_boardcolumnentity PRIMARY KEY (id)
);

CREATE TABLE board_column_entity_cards
(
    board_column_entity_id BIGINT NOT NULL,
    cards_id               BIGINT NOT NULL
);

CREATE TABLE board_entity
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_boardentity PRIMARY KEY (id)
);

CREATE TABLE board_entity_board_columns
(
    board_entity_id  BIGINT NOT NULL,
    board_columns_id BIGINT NOT NULL
);

CREATE TABLE card_entity
(
    id              BIGINT NOT NULL,
    title           VARCHAR(255),
    description     VARCHAR(255),
    board_column_id BIGINT,
    CONSTRAINT pk_cardentity PRIMARY KEY (id)
);

ALTER TABLE board_column_entity_cards
    ADD CONSTRAINT uc_board_column_entity_cards_cards UNIQUE (cards_id);

ALTER TABLE board_entity_board_columns
    ADD CONSTRAINT uc_board_entity_board_columns_boardcolumns UNIQUE (board_columns_id);

ALTER TABLE board_column_entity
    ADD CONSTRAINT FK_BOARDCOLUMNENTITY_ON_BOARD FOREIGN KEY (board_id) REFERENCES board_entity (id);

ALTER TABLE card_entity
    ADD CONSTRAINT FK_CARDENTITY_ON_BOARDCOLUMN FOREIGN KEY (board_column_id) REFERENCES board_column_entity (id);

ALTER TABLE board_column_entity_cards
    ADD CONSTRAINT fk_boacolentcar_on_board_column_entity FOREIGN KEY (board_column_entity_id) REFERENCES board_column_entity (id);

ALTER TABLE board_column_entity_cards
    ADD CONSTRAINT fk_boacolentcar_on_card_entity FOREIGN KEY (cards_id) REFERENCES card_entity (id);

ALTER TABLE board_entity_board_columns
    ADD CONSTRAINT fk_boaentboacol_on_board_column_entity FOREIGN KEY (board_columns_id) REFERENCES board_column_entity (id);

ALTER TABLE board_entity_board_columns
    ADD CONSTRAINT fk_boaentboacol_on_board_entity FOREIGN KEY (board_entity_id) REFERENCES board_entity (id);