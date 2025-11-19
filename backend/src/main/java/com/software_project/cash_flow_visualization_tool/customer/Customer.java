package com.software_project.cash_flow_visualization_tool.customer;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Customer implements Serializable {
    @Id
    @Column(name = "customer_id", nullable = false, length = 15)
    private String customerId;

    @Column(name = "customer_name", nullable = false, length = 140)
    private String customerName;

    @Column(name = "customer_type", nullable = false, length = 7)
    private String customerType;
}