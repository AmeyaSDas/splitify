package com.splitify.splitify.transaction.repository;

import com.splitify.splitify.transaction.domain.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository
    extends JpaRepository<ExpenseEntity, Integer>, ExpenseRepositoryCustom {
  /**
   * Find by group id and paid by.
   *
   * @param groupId groupId
   * @param paidBy paidBy
   * @return expense entity
   */
  List<ExpenseEntity> findByGroupIdAndPaidBy(Integer groupId, Integer paidBy);

  /**
   * fnd by group id and payment status
   *
   * @param groupId groupId
   * @param paymentStatus paymentStatus
   * @return expense entity
   */
  List<ExpenseEntity> findByGroupIdAndPaymentStatusIn(Integer groupId, List<Integer> paymentStatus);
}
