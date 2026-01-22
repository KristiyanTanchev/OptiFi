package com.optifi.domain.reporting.repository;

import com.optifi.domain.shared.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportJdbcRepositoryImpl implements ReportJdbcRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public ReportSummaryAgg getReportSummary(Long userId, Currency currency, Instant from, Instant to) {

        StringBuilder sql = new StringBuilder("""
                select
                    coalesce(sum(case when t.amount > 0 then t.amount else 0 end), 0) as income,
                    coalesce(sum(case when t.amount < 0 then -t.amount else 0 end), 0) as expense,
                    count(t.id) as count
                from transactions t
                join accounts a on a.id = t.account_id
                where a.user_id = :userId and a.currency = :currency
                """);
        MapSqlParameterSource p = new MapSqlParameterSource();
        p.addValue("userId", userId);
        p.addValue("currency", currency.name());

        if (from != null) {
            sql.append(" and t.occurred_at >= :from");
            p.addValue("from", from);
        }
        if (to != null) {
            sql.append(" and t.occurred_at < :to");
            p.addValue("to", to);
        }

        return jdbc.queryForObject(sql.toString(), p,
                (rs, __) -> new ReportSummaryAgg(
                        rs.getBigDecimal("income"),
                        rs.getBigDecimal("expense"),
                        rs.getLong("count")
                )
        );
    }

    @Override
    public List<ReportSummaryByAccountAgg> getReportSummaryByAccount(Long userId, Currency currency, Instant from, Instant to) {

        StringBuilder sql = new StringBuilder("""
                    select
                      a.id as account_id,
                      a.name as account_name,
                      coalesce(sum(case when t.amount > 0 then t.amount else 0 end), 0) as income,
                      coalesce(sum(case when t.amount < 0 then -t.amount else 0 end), 0) as expense,
                      count(t.id) as count
                    from transactions t
                    join accounts a on a.id = t.account_id
                    where a.user_id = :userId and a.currency = :currency
                """);

        MapSqlParameterSource p = new MapSqlParameterSource();
        p.addValue("userId", userId);
        p.addValue("currency", currency.name());

        if (from != null) {
            sql.append(" and t.occurred_at >= :from");
            p.addValue("from", from);
        }
        if (to != null) {
            sql.append(" and t.occurred_at < :to");
            p.addValue("to", to);
        }

        sql.append("""
                    group by a.id, a.name
                    order by a.name
                """);

        return jdbc.query(sql.toString(), p,
                (rs, __) -> new ReportSummaryByAccountAgg(
                        rs.getLong("account_id"),
                        rs.getString("account_name"),
                        rs.getBigDecimal("income"),
                        rs.getBigDecimal("expense"),
                        rs.getLong("count")
                )
        );
    }
}
