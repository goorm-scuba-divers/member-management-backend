package io.goorm.member.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static io.goorm.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> findAllByPageableAndFilter(Pageable pageable, String nickname) {

       JPAQuery<Member> query = queryFactory
                .select(member)
                .from(member)
                .where(
                        nicknameContains(nickname),
                        member.deletedAt.isNull()
                )
               .offset(pageable.getOffset())
               .limit(pageable.getPageSize());

       PathBuilder<?> path = new PathBuilder<>(member.getType(), member.getMetadata().getName());

        // ✅ 정렬 처리
        for (Sort.Order order : pageable.getSort()) {
            query.orderBy(
                    new OrderSpecifier<>(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            path.getComparable(order.getProperty(), Comparable.class)
                    )
            );
        }

        List<Member> content = query.fetch();
        long total = Optional.ofNullable(
                queryFactory
                        .select(member.count())
                        .from(member)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);

    }

    private BooleanExpression nicknameContains(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return null; // No filter applied
        }
        return member.nickname.contains(nickname);
    }
}
