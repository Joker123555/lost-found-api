package com.campus.lostfound.repository;

import com.campus.lostfound.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneAndIsDeleted(String phone, int isDeleted);

    Optional<User> findByAccountAndIsDeleted(String account, int isDeleted);

    Optional<User> findByOpenidAndIsDeleted(String openid, int isDeleted);

    @Query("SELECT u FROM User u WHERE u.isDeleted = 0 AND (:kw IS NULL OR :kw = '' OR u.nickname LIKE CONCAT('%', :kw, '%') OR u.openid LIKE CONCAT('%', :kw, '%') OR u.phone LIKE CONCAT('%', :kw, '%') OR u.account LIKE CONCAT('%', :kw, '%'))")
    Page<User> search(@Param("kw") String kw, Pageable pageable);

    /** 仅统计未删除用户，用于手机号唯一校验（与 uk_users_phone 一致：已删除用户应释放 phone） */
    @Query("SELECT COUNT(u) FROM User u WHERE u.phone = :phone AND u.isDeleted = 0")
    long countActiveByPhone(@Param("phone") String phone);

    @Query("SELECT COUNT(u) FROM User u WHERE u.phone = :phone AND u.isDeleted = 0 AND u.id <> :id")
    long countActiveByPhoneExceptId(@Param("phone") String phone, @Param("id") Long id);
}
