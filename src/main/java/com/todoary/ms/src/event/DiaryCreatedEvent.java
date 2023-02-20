package com.todoary.ms.src.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@ToString
public class DiaryCreatedEvent {
    private final Long memberId;
    private final LocalDate diaryDate;
}
