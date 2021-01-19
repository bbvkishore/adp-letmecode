package com.adp.interview.monetary.component;

import com.adp.interview.monetary.dto.CoinDTO;
import com.adp.interview.monetary.exception.BillChangeException;
import com.adp.interview.monetary.model.CoinsModel;
import com.adp.interview.monetary.type.CoinsType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public class BillChangeComponent {

    @Value("${bill.exchange.coins.quantity}")
    private String quantityValue;

    @Value("${bill.exchange.coins.strategy}")
    private String coinsStrategy;

    public BillChangeComponent() {
    }


    public CoinsModel getChange(CoinsModel coins, Integer bill) throws BillChangeException {
        // convert the list to coin DTO object
        List<CoinDTO> coinDTOList = getCoinDTOS(coins);
        // consider only available coins
        coinDTOList = getAvailableCoins(coinDTOList);

        if (CollectionUtils.isEmpty(coinDTOList)){
            throw new BillChangeException("Coins depleted to Zero count");
        }

        BigDecimal billInCents = new BigDecimal(100)
                .multiply(new BigDecimal(bill));

        for (CoinDTO coinDTO : coinDTOList){
            Integer coinDenominationNeeded = billInCents.divide(
                    new BigDecimal(coinDTO.getCoinsType().value))
                    .intValue();


            if (coinDenominationNeeded <= coinDTO.getAvailableCount()){
                billInCents = billInCents.remainder(new BigDecimal(coinDTO.getCoinsType().value));
                coinDTO.setAvailableCount(coinDTO.getAvailableCount() - coinDenominationNeeded );
            } else {
                billInCents = billInCents.subtract(
                        new BigDecimal(coinDTO.getCoinsType().value)
                                .multiply(new BigDecimal(coinDTO.getAvailableCount())));
                coinDTO.setAvailableCount(0);
            }
        }


        // After spending all the available change, if we still have +ve bill
        // that means, funds are depleted and we cannot give change
        if (billInCents.compareTo(BigDecimal.ZERO) != 0){
            throw new BillChangeException(MessageFormat.format("Coins are depleted and are " +
                    "not available to process the change{0} for the remaining bill (in cents) {1}",
                    coinDTOList, billInCents));
        }
        convertCoinsDTOToCoins(coinDTOList, coins);
        return coins;

    }

    private CoinsModel convertCoinsDTOToCoins(List<CoinDTO> coinDTOList, CoinsModel coins) throws BillChangeException{
        for (CoinDTO coinDTO: coinDTOList){
            switch (coinDTO.getCoinsType()) {
                case QUARTERS:
                    coins.setQuarters(coinDTO.getAvailableCount());
                    break;
                case DIMES:
                    coins.setDimes(coinDTO.getAvailableCount());
                    break;
                case NICKELS:
                    coins.setNickels(coinDTO.getAvailableCount());
                    break;
                case PENNIES:
                    coins.setPennies(coinDTO.getAvailableCount());
                    break;
                default:
                    throw new BillChangeException("Invalid Coin Type");
            }
        }
        return coins;
    }


    private List<CoinDTO> getCoinDTOS(CoinsModel coins) {
        return Arrays.asList(
                new CoinDTO(CoinsType.QUARTERS, coins.getQuarters()),
                new CoinDTO(CoinsType.DIMES, coins.getDimes()),
                new CoinDTO(CoinsType.NICKELS, coins.getNickels()),
                new CoinDTO(CoinsType.PENNIES, coins.getPennies())
        );
    }

    private List<CoinDTO> getAvailableCoins(List<CoinDTO> coinDTOList) {
        List<CoinDTO> resultantDTO =   coinDTOList.stream()
                .filter(coinDTO -> coinDTO.getAvailableCount() > 0)
                .collect(Collectors.toList());
        if (coinsStrategy != null && coinsStrategy.equals("ASC")) {
            Collections.reverse(resultantDTO);
        }

        return resultantDTO;
    }



    public CoinsModel buildCoins() {
        Integer quantity = Integer.valueOf(quantityValue);
        CoinsModel resetCoins = new CoinsModel();
        resetCoins.setDimes(quantity);
        resetCoins.setNickels(quantity);
        resetCoins.setQuarters(quantity);
        resetCoins.setPennies(quantity);
        return resetCoins;
    }
}
