package com.adp.interview.monetary.dto;

import com.adp.interview.monetary.type.CoinsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoinDTO {
    private CoinsType coinsType;
    private Integer availableCount;
}
