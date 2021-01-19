package com.adp.interview.monetary.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(NON_NULL)
public class CoinsModel implements Serializable {
    private Integer quarters;
    private Integer dimes;
    private Integer nickels;
    private Integer pennies;
}
