package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @JsonProperty("user_id")
    @Min(value = 1, message = "tối thiểu 1 ký tụ")
    private Long userID;

    @JsonProperty("fullname")
    private String fullName;

    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Không được bỏ trống SĐT")
    @Size(min = 5, message = "SĐT tối thiểu 5 ký tự")
    private String phoneNumber;

    private String address;

    private String note;

    @JsonProperty("order_date")
    private Date  orderDate;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Tổng tiền >=0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("paying_method")
    private String payingMethod;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;




}
