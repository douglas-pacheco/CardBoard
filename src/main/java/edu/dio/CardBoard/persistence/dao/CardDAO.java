package edu.dio.CardBoard.persistence.dao;

import edu.dio.CardBoard.dto.CardDetailsDTO;
import edu.dio.CardBoard.persistence.entity.BoardColumnEntity;
import edu.dio.CardBoard.persistence.entity.CardEntity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static edu.dio.CardBoard.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@Component
public class CardDAO {

    private final DataSource dataSource;
    @Getter
    private final Connection connection;

    @Autowired
    public CardDAO(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
    }

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO card_entity (title, description, board_column_id) values (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i ++, entity.getTitle());
            statement.setString(i ++, entity.getDescription());
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();
        }
        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException{
        var sql = "UPDATE card_entity SET board_column_id = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i ++, columnId);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       bc.board_id,
                       (SELECT COUNT(sub_b.id)
                               FROM block_entity sub_b
                              WHERE sub_b.card_id = c.id) blocks_amount
                  FROM card_entity c
                  LEFT JOIN block_entity b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL
                 INNER JOIN board_column_entity bc
                    ON bc.id = c.board_column_id
                  WHERE c.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name"),
                        resultSet.getLong("bc.board_id")

                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }



    public List<CardEntity> findAllByColumn(final BoardColumnEntity column) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       c.board_column_id
                  FROM card_entity c
                  WHERE c.board_column_id = ?;
                """;
        List<CardEntity> cards = new ArrayList<>();
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, column.getId());
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){

                var dto = new CardEntity(resultSet.getLong("id"),
                                        resultSet.getString("title"),
                                        resultSet.getString("description"),
                                        resultSet.getLong("board_id"),
                                        column);
                cards.add(dto);
            }
            return cards;

        }
    }

}
