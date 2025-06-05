package io.goorm.member.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.goorm.member.dto.response.PageResponse;
import io.micrometer.observation.Observation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static io.goorm.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> findAllByPageableAndFilter(Pageable pageable, String searchValue, MemberRole role) {

       JPAQuery<Member> query = queryFactory
                .select(member)
                .from(member)
                .where(
                        searchValueContains(searchValue),
                        member.deletedAt.isNull(),
                        roleEquals(role)
                )
               .offset(pageable.getOffset())
               .limit(pageable.getPageSize());

       PathBuilder<?> path = new PathBuilder<>(member.getType(), member.getMetadata().getName());

        for (Sort.Order order : pageable.getSort()) {
            query.orderBy(
                    new OrderSpecifier<>(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            path.getComparable(order.getProperty(), Comparable.class)
                    )
            );
        }

        List<Member> content = query.fetch();

        JPAQuery<Long> count = queryFactory
                .select(member.count())
                .from(member);

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression searchValueContains(String searchValue) {

        if (searchValue == null || searchValue.isEmpty()) {
            return null;
        }

        BooleanExpression searchExpression = member.username.containsIgnoreCase(searchValue);

        if (StringUtils.isNumeric(searchValue)) {
            searchExpression = searchExpression
                    .or(member.id.eq(Long.valueOf(searchValue)));
        }

        return searchExpression;
    }

    private BooleanExpression roleEquals(MemberRole role) {
        return role != null ? member.role.eq(role) : null;
    }
}
