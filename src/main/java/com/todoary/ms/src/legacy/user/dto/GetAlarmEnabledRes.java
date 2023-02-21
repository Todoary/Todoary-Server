package com.todoary.ms.src.legacy.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"userId","isTodoAlarmChecked","isDiaryAlarmChecked","isRemindAlarmChecked"})
@JsonIgnoreProperties({"todoAlarmChecked", "diaryAlarmChecked", "remindAlarmChecked"}) // 중복 방지

public class GetAlarmEnabledRes {

    private Long userId;
    @JsonProperty("isTodoAlarmChecked")
    private boolean isTodoAlarmChecked;
    @JsonProperty("isDiaryAlarmChecked")
    private boolean isDiaryAlarmChecked;
    @JsonProperty("isRemindAlarmChecked")
    private boolean isRemindAlarmChecked;


}
