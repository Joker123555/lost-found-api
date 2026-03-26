package com.campus.lostfound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequest {
    @NotNull
    private Integer type;
    @NotNull
    private Long categoryId;
    @NotBlank
    @Size(max = 64)
    private String title;
    @NotBlank
    @Size(max = 2000)
    private String description;
    @NotBlank
    private String location;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime happenedAt;
    @NotBlank
    private String contactName;
    @NotBlank
    private String contactPhone;
    private List<String> imageUrls;
}
