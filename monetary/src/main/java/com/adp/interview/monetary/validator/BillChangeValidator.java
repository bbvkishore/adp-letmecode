package com.adp.interview.monetary.validator;

import com.adp.interview.monetary.domain.Coins;
import com.adp.interview.monetary.model.BillChangeRequest;
import com.adp.interview.monetary.type.BillsType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Component
public class BillChangeValidator {

    public boolean validate(Integer bill){
        if (bill == null || bill <= 0){
            return false;
        }
        return Arrays.stream(BillsType.values())
                .map(BillsType::getValue)
                .map(Integer::valueOf)
                .anyMatch(x -> Objects.equals(x, bill));
    }

}
