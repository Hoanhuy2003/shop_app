package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
//@Service
//@RequiredArgsConstructor
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank(message = "Bắt buộc phải nhập tên")
    @Size(min = 3, max = 200 , message = "Tên phải từ 3 đên 200 ký tự")
    private String name;

    @Min(value = 0 , message = "Giá tiền >=0")
    @Max(value = 1000000, message = "Nhỏ hơn 1000000")
    private Float price;
    private String thumbnail;
    private String description;



    @JsonProperty("category_id")
    private Long categoryId;

    private List<MultipartFile> files;


}
