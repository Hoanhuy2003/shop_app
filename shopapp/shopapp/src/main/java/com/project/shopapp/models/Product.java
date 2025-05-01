package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự tăng lên 1 mỗi khi thêm mới
    private Long id;

    @Column(name = "name", nullable = false, length = 350) // bắt buộc phải nhaap
    private String name;

    private Float price;

    @Column(name = "thumbnail", length = 300)
    private String thumbnail;

    @Column(name = "description")
    private String description;



    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;



}
