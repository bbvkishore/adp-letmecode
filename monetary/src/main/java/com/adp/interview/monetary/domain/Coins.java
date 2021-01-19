package com.adp.interview.monetary.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "coins")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Coins {
    @Id
    private String id;
    private Integer quarters;
    private Integer dimes;
    private Integer nickels;
    private Integer pennies;

}
