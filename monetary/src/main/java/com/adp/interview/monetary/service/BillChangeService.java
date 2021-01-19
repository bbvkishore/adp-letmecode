package com.adp.interview.monetary.service;

import com.adp.interview.monetary.domain.Coins;
import com.adp.interview.monetary.dto.CoinDTO;
import com.adp.interview.monetary.exception.BillChangeException;
import com.adp.interview.monetary.model.BillChangeResponse;
import com.adp.interview.monetary.model.CoinsModel;
import com.adp.interview.monetary.repository.CoinsRepository;
import com.adp.interview.monetary.type.CoinsType;
import com.adp.interview.monetary.validator.BillChangeValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillChangeService {

    private CoinsRepository coinsRepository;
    private BillChangeValidator billChangeValidator;

    @Value("${bill.exchange.coins.quantity}")
    private String quantityValue;

    public BillChangeService(CoinsRepository coinsRepository,
                             BillChangeValidator billChangeValidator
                             ) {
        this.coinsRepository = coinsRepository;
        this.billChangeValidator = billChangeValidator;
    }

    /**
     * Get the change from billing Request
     * @param  bill bill
     * @return
     */
    public BillChangeResponse getChange(Integer bill) throws BillChangeException {
        List<Coins> coinsList = coinsRepository.findAll();
        // Make sure the list is just one
        if (coinsList == null || coinsList.size() != 1){
            throw new BillChangeException("Coin Retrieval incorrect");
        }

        CoinsModel coinsModel = getChange(coinsList.get(0), bill);
        BillChangeResponse billChangeResponse = new BillChangeResponse();
        billChangeResponse.setCoins(coinsModel);
        return billChangeResponse;
    }

    /**
     * Get the status
     * @return
     */
    public BillChangeResponse getStatus(){
        List<Coins> coinsList = coinsRepository.findAll();
        if (coinsList == null || coinsList.size() != 1){
            throw new BillChangeException("Coin Retrieval incorrect");
        }
        BillChangeResponse billChangeResponse = getBillChangeResponse(coinsList.get(0));
        return billChangeResponse;
    }

    public CoinsModel getChange(Coins coins, Integer bill) throws BillChangeException {
        CoinsModel coinsModel = new CoinsModel();
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
                convertCoinDTOToModel(coinDTO, coinsModel, coinDenominationNeeded);
                coinDTO.setAvailableCount(coinDTO.getAvailableCount() - coinDenominationNeeded );
            } else {
                billInCents = billInCents.subtract(
                        new BigDecimal(coinDTO.getCoinsType().value)
                                .multiply(new BigDecimal(coinDTO.getAvailableCount())));
                convertCoinDTOToModel(coinDTO, coinsModel, coinDTO.getAvailableCount());
                coinDTO.setAvailableCount(0);
            }
            if (billInCents.compareTo(BigDecimal.ZERO) == 0){
                break;
            }
        }
        // After spending all the available change, if we still have +ve bill
        // that means, funds are depleted and we cannot give change
        if (billInCents.compareTo(BigDecimal.ZERO) == 1){
            throw new BillChangeException("Coins are depleted and are not available to process the change");
        }
        convertCoinsDTOToCoins(coinDTOList, coins);
        coinsRepository.save(coins);
        return coinsModel;

    }

    private Coins convertCoinsDTOToCoins(List<CoinDTO> coinDTOList, Coins coins) throws BillChangeException{
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

    private CoinsModel convertCoinDTOToModel(CoinDTO coinDTO, CoinsModel coinModel, Integer coinQuantity) throws BillChangeException{
            switch (coinDTO.getCoinsType()) {
                case QUARTERS:
                    coinModel.setQuarters(coinQuantity);
                    break;
                case DIMES:
                    coinModel.setDimes(coinQuantity);
                    break;
                case NICKELS:
                    coinModel.setNickels(coinQuantity);
                    break;
                case PENNIES:
                    coinModel.setPennies(coinQuantity);
                    break;
                default:
                    throw new BillChangeException("Invalid Coin Type");
            }

        return coinModel;
    }


    private List<CoinDTO> getCoinDTOS(Coins coins) {
        return Arrays.asList(
                new CoinDTO(CoinsType.QUARTERS, coins.getQuarters()),
                new CoinDTO(CoinsType.DIMES, coins.getDimes()),
                new CoinDTO(CoinsType.NICKELS, coins.getNickels()),
                new CoinDTO(CoinsType.PENNIES, coins.getPennies())
        );
    }

    private List<CoinDTO> getAvailableCoins(List<CoinDTO> coinDTOList) {
        return coinDTOList.stream()
                .filter(coinDTO -> coinDTO.getAvailableCount() > 0)
                .collect(Collectors.toList());
    }

    private BillChangeResponse getBillChangeResponse(Coins coins) {

        CoinsModel coinDTO = new CoinsModel();
        BeanUtils.copyProperties(coins, coinDTO);

        BillChangeResponse billChangeResponse = new BillChangeResponse();
        billChangeResponse.setCoins(coinDTO);
        return billChangeResponse;
    }

    public void reset(){
        List<Coins> coins = coinsRepository.findAll();
        Optional<Coins> optionalCoins = coins.stream().findFirst();
        Integer quantity = Integer.valueOf(quantityValue);
        if (optionalCoins.isPresent()){
            Coins resetCoins = optionalCoins.get();
            resetCoins.setDimes(quantity);
            resetCoins.setNickels(quantity);
            resetCoins.setQuarters(quantity);
            resetCoins.setPennies(quantity);
            coinsRepository.save(resetCoins);
        }
    }
}
