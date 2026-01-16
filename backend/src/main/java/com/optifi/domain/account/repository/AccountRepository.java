package com.optifi.domain.account.repository;

import com.optifi.domain.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUserId(long userId);

    boolean existsByUserIdAndName(long l, String name);

    boolean existsByIdAndUserId(long accountId, long userId);
}
