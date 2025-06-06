package edu.dio.CardBoard.persistence.dao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static edu.dio.CardBoard.persistence.converter.OffsetDateTimeConverter.toTimestamp;


@Getter
@Component
public class BlockDAO {

    private final Connection connection;

    @Autowired
    public BlockDAO(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
    }

    public OffsetDateTime block(final String reason, final Long cardId) throws SQLException {
        var sql = "INSERT INTO block_entity (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        Timestamp blockTimestamp = toTimestamp(OffsetDateTime.now());
        assert blockTimestamp != null;
        OffsetDateTime blockOffsetDateTime= blockTimestamp.toInstant().atOffset(OffsetDateTime.now().getOffset());

        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, blockTimestamp);
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
            return blockOffsetDateTime;
        }
    }

    public void unblock(final String reason, final Long cardId) throws SQLException{
        var sql = "UPDATE block_entity SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

}
