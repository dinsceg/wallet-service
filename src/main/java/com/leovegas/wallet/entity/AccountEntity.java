package com.leovegas.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@Table(name = "account", uniqueConstraints = {@UniqueConstraint(columnNames = {"account_number"})},
        indexes = {@Index(name = "idx_account_number", columnList = "account_number")})
@NoArgsConstructor
@AllArgsConstructor
public final class AccountEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "idgen")
    @SequenceGenerator(sequenceName = "id", name = "idgen", allocationSize = 1)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Version
    private Long version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

}
