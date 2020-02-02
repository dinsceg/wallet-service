package com.leovegas.wallet.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@Table(name = "transaction", uniqueConstraints = {@UniqueConstraint(columnNames = {"transaction_id"})},
        indexes = {
                @Index(name = "idx_transaction_id", columnList = "transaction_id"),
                @Index(name = "idx_transaction_type", columnList = "transaction_type"),
                @Index(name = "idx_created_date", columnList = "created_date")})
@NoArgsConstructor
@AllArgsConstructor
public final class TransactionEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "idgen")
    @SequenceGenerator(sequenceName = "id", name = "idgen", allocationSize = 1)
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Column(name = "account_number", nullable = false, updatable = false)
    private String accountNumber;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, updatable = false)
    private String currency;

    @Version
    private Long version;

}
