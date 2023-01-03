package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
