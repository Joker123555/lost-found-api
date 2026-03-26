package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotBlank
    private String content;
    /** 0=文本，1=图片 */
    private Integer type;
}
