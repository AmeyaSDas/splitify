package com.splitify.splitify.transaction.domain;

import com.splitify.splitify.transaction.enums.ExpenseSharePaymentStatus;
import com.splitify.splitify.transaction.enums.ExpenseShareStatus;
import com.splitify.splitify.transaction.enums.ExpenseStatus;
import com.splitify.splitify.transaction.service.ExpenseRequest;
import com.splitify.splitify.transaction.service.ShareDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EXPENSE")
@Builder
public class ExpenseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "EXPENSEID")
  private Integer expenseId;

  @Column(name = "EXPENSENAME")
  private String expenseName;

  @Column(name = "GROUPID")
  private Integer groupId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "STATUS")
  private Integer status;

  @Column(name = "PAYMENTSTATUS")
  private Integer paymentStatus;

  @Column(name = "ONDATE")
  private Calendar onDate;

  @Column(name = "PAIDBY")
  private Integer paidBy;

  @Column(name = "CREATEDBY")
  private Integer createdBy;

  @OneToMany(
      mappedBy = "expense",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<ExpenseShareEntity> expenseShare;

  /**
   * Add expense share.
   *
   * @param shareDetails shareDetails.
   */
  public void addExpenseShare(List<ShareDetails> shareDetails) {
    if (expenseShare == null) {
      expenseShare = new ArrayList<>();
    }
    if (shareDetails != null) {
      shareDetails.forEach(
          share -> {
            expenseShare.add(
                ExpenseShareEntity.builder()
                    .amount(share.getAmount())
                    .owedBy(share.getOwnerId())
                    .settledAmount(BigDecimal.ZERO)
                    .status(ExpenseShareStatus.ACTIVE.getCode())
                    .paymentStatus(ExpenseSharePaymentStatus.UNSETTLED.getCode())
                    .expense(this)
                    .build());
          });
    }
  }

  /**
   * Updates the expense
   *
   * @param expenseRequest expenseRequests
   */
  public void updateExpense(ExpenseRequest expenseRequest) {
    setAmount(expenseRequest.getAmount());
    setGroupId(expenseRequest.getGroupId());
    setExpenseName(expenseRequest.getExpenseName());
    setPaidBy(expenseRequest.getPaidBy());
    setOnDate(expenseRequest.getOnDate());
    updateExpenseShare(expenseRequest.getShare());
  }

  /**
   * Updates the expense share
   *
   * @param share share
   */
  private void updateExpenseShare(List<ShareDetails> share) {
    Map<Integer, BigDecimal> updateIds =
        share.stream().collect(Collectors.toMap(ShareDetails::getOwnerId, ShareDetails::getAmount));
    Set<Integer> oldIds =
        expenseShare.stream().map(ExpenseShareEntity::getOwedBy).collect(Collectors.toSet());
    List<ExpenseShareEntity> oldShares = new ArrayList<>();
    List<ShareDetails> newShares = new ArrayList<>();
    expenseShare.forEach(
        expenseShareEntity -> {
          if (!updateIds.containsKey(expenseShareEntity.getOwedBy())) {
            oldShares.add(expenseShareEntity);
          } else {
            expenseShareEntity.setAmount(updateIds.get(expenseShareEntity.getOwedBy()));
          }
        });
    share.forEach(
        shareDetails -> {
          if (!oldIds.contains(shareDetails.getOwnerId())) {
            newShares.add(shareDetails);
          }
        });
    if (!CollectionUtils.isEmpty(newShares)) {
      addExpenseShare(newShares);
    }
    if (!CollectionUtils.isEmpty(oldShares)) {
      expenseShare.removeAll(oldShares);
    }
  }

  /** Delete expense. */
  public void delete() {
    setStatus(ExpenseStatus.CANCELLED.getCode());
    expenseShare.forEach(expense -> expense.setStatus(ExpenseShareStatus.CANCELLED.getCode()));
  }
}
