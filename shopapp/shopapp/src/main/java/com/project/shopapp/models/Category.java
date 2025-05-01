package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="categories")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự tăng lên 1 mỗi khi thêm mới
    private Long id;

    @Column(name = "name", nullable = false) // bắt buộc phải nhaap
    private String name;




}
