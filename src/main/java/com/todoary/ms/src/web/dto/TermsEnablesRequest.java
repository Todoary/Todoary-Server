package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TermsEnablesRequest {
    @JsonProperty("isChecked")
    private boolean isChecked;
}
