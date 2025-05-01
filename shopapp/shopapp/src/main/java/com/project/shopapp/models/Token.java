package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "tokens")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự tăng lên 1 mỗi khi thêm mới
    private Long id;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "token_type",length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private Date expirationDate;

    private boolean revoked;
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
