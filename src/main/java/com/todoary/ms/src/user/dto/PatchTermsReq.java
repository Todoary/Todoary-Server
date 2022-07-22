package com.todoary.ms.src.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchTermsReq {

    @JsonProperty("isChecked")
    private boolean isChecked;

}
