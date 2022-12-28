package com.todoary.ms.src.domain.alarm;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("remind")
public class RemindAlarm extends Alarm {
    private LocalDate targetDate;
}
