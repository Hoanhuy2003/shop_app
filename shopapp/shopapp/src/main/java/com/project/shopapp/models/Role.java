package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự tăng lên 1 mỗi khi thêm mới
    private Long id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    public static String ADMIN = "ADMIN";
    public static String USER = "USER";


}
