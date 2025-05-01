package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "ID phải > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "ID phải > 0")
    private Long productId;

    @Min(value = 0, message = "Giá >=0")
    private Float price;

    @Min(value = 1, message = "Số sản phẩm > 0")
    @JsonProperty("number_of_product")
    private int numberOfProducts;

    @Min(value = 0, message = "Tổng tiền >=0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;


}
