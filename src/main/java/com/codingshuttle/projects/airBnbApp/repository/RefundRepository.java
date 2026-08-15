package com.codingshuttle.projects.airBnbApp.repository;

import com.codingshuttle.projects.airBnbApp.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByRazorpayRefundId(String refundId);
}