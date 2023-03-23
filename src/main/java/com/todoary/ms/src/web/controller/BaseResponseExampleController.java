package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.common.response.BaseResponseStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Comparator;

@Controller
public class BaseResponseExampleController {

    @GetMapping("/responses")
    public String retrieveBaseResponseStatuses(Model model) {
        model.addAttribute("responses", Arrays.stream(BaseResponseStatus.values()).sorted(Comparator.comparingInt(BaseResponseStatus::getCode)).toArray());
        return "responses";
    }
}
