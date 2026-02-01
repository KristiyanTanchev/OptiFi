package com.optifi.domain.reporting.repository;

import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportCategoriesByCatAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryAgg;
import com.optifi.domain.reporting.repository.aggregations.ReportSummaryByAccountAgg;
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

    @Override
    public ReportCategoriesAgg getReportCategories(Long userid, Integer sign, Instant from, Instant to) {
        StringBuilder sql = new StringBuilder("""
                select
                  ABS(COALESCE(SUM(t.amount),0)) as total
                from transactions t
                join accounts a on a.id = t.account_id
                where a.user_id = :userId
                """);
        MapSqlParameterSource p = new MapSqlParameterSource();
        p.addValue("userId", userid);
        if (sign != null) {
            sql.append(" and t.amount ").append(sign > 0 ? ">" : "<").append(" 0");
        }
        if (from != null) {
            sql.append(" and t.occurred_at >= :from");
            p.addValue("from", from);
        }
        if (to != null) {
            sql.append(" and t.occurred_at < :to");
            p.addValue("to", to);
        }

        return jdbc.queryForObject(sql.toString(), p,
                (rs, __) -> new ReportCategoriesAgg(
                        rs.getBigDecimal("total")
                ));


    }

    @Override
    public List<ReportCategoriesByCatAgg> getReportCategoriesByCat(Long userId, Integer sign, Instant from, Instant to) {
        StringBuilder sql = new StringBuilder("""
                select
                  c.id as category_id,
                  c.name as category_name,
                  c.icon as icon,
                  ABS(COALESCE(SUM(t.amount),0)) as amount
                from transactions t
                join accounts a on a.id = t.account_id
                join categories c on c.id = t.category_id
                where a.user_id = :userId
                """);
        MapSqlParameterSource p = new MapSqlParameterSource();
        p.addValue("userId", userId);
        if (sign != null) {
            sql.append(" and t.amount ").append(sign > 0 ? ">" : "<").append(" 0");
        }
        if (from != null) {
            sql.append(" and t.occurred_at >= :from");
            p.addValue("from", from);
        }
        if (to != null) {
            sql.append(" and t.occurred_at < :to");
            p.addValue("to", to);
        }
        sql.append(" group by c.id, c.name, c.icon order by amount desc");
        return jdbc.query(sql.toString(), p,
                (rs, __) -> new ReportCategoriesByCatAgg(
                        rs.getLong("category_id"),
                        rs.getString("category_name"),
                        rs.getString("icon"),
                        rs.getBigDecimal("amount")
                ));
    }
}
