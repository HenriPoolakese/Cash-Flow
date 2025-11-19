package com.software_project.cash_flow_visualization_tool.transaction;

import com.software_project.cash_flow_visualization_tool.customer.Customer;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false) //added back, because transaction_id is not unique
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "customer_iban", nullable = false, length = 34)
    private String customerIban;

    @Column(name = "counterparty_iban", nullable = false, length = 34)
    private String counterpartyIban;

    @Column(name = "counterparty_id", length = 15)
    private String counterpartyId;

    @Column(name = "counterparty_name", nullable = false, length = 140)
    private String counterpartyName;

    @Column(name = "counterparty_country", length = 3)
    private String counterpartyCountry;

    @Column(name = "counterparty_bank_name", nullable = false, length = 45)
    private String counterpartyBankName;

    @Column(name = "counterparty_bank_country", nullable = false, length = 3)
    private String counterpartyBankCountry;

    @Column(name = "counterparty_bank_bic_code", nullable = false, length = 20)
    private String counterpartyBankBicCode;

    @Column(name = "counterparty_type", nullable = false, length = 20)
    private String counterpartyType;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "amount_org", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountOrg; // Original transaction amount

    @Column(name = "amount_eur", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountEur;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency; // Currency (e.g., 'USD', 'EUR')

    @Column(name = "dc", nullable = false, length = 1)
    private char dc;  // Debit ('D') or Credit ('C')

    @Column(name = "transaction_scope", nullable = false, length = 1)
    private char transactionScope;  // Transaction scope ('I', 'D', 'F')

    @Column(name = "channel_type", nullable = false, length = 25)
    private String channelType;  // Channel type (e.g., 'Mobile App')

    @Column(name = "channel_code", nullable = false)
    private Short channelCode; // Channel code (e.g., '01')

    @Column(name = "description", nullable = false, length = 210)
    private String description; // Transaction description

    @Column(name = "fee_f", nullable = false)
    private Boolean feeF; // false - no fee, true - fee

    @Column(name = "fee_type", length = 3)
    private String feeType; // Fee type (e.g., 'SHA' - shared, 'OUR' - sender pays, 'BEN' - beneficiary pays)

    @Column(name = "is_rvrs_f", nullable = false)
    private Boolean isRvrsF = false; // Reverse flag, this is reversing transaction

    @Column(name = "is_rvrs_orig_id")
    private Long isRvrsOrigId; // original transaction_id

    @Column(name = "was_later_rvrs_f", nullable = false)
    private Boolean wasLaterRvrsF = false; // flag to indicate that this transaction was later reversed
}
