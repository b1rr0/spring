package ronin.ua.balance.processor.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import ronin.ua.balance.processor.entites.UserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Modifying
    @Transactional
    @Retryable(
            value = { Exception.class },
            backoff = @Backoff(delay = 2000)
    )
    @Query(value = """
    UPDATE users u
    SET balance = up.balance
    FROM (
        SELECT id, balance
        FROM unnest(:ids, :balances) AS t(id, balance)
    ) AS up
    WHERE u.id = up.id
    RETURNING u.id, u.balance,u.name
    """, nativeQuery = true)
    List<UserEntity> batchUpdateAndReturnProcessed(@Param("ids") UUID[] ids, @Param("balances") Double[] balances);

    @Modifying
    @Retryable(
            value = { Exception.class },
            backoff = @Backoff(delay = 2000)
    )
    @Transactional
    @Query(value = """
    UPDATE users AS u
    SET balance = up.balance
    FROM (
        SELECT unnest(:ids) AS id, unnest(:balances) AS balance
    ) AS up
    WHERE u.id = up.id
    """, nativeQuery = true)
    void batchUpdate(@Param("ids") UUID[] ids, @Param("balances") Double[] balances);

    @Modifying
    @Transactional
    @Query(value = """
    WITH ins AS (
        INSERT INTO users (id, name, balance)
        SELECT 
            gen_random_uuid(),
            md5(random()::text) AS name,
            :balance
        FROM generate_series(1, :count)
        RETURNING id
    )
    SELECT id FROM ins
    """, nativeQuery = true)
    List<UUID> insertRandomUsers(@Param("count") int count, @Param("balance") Double balance);
}

