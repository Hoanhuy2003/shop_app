package com.project.shopapp.responses;

import com.project.shopapp.models.ProductImage;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    private String imageUrl;

    public static ProductImageResponse fromProductImage(ProductImage productImage) {
        return ProductImageResponse.builder()
                .imageUrl(productImage.getImageUrl())
                .build();
    }
}
