package com.adp.interview.monetary.controller;

import com.adp.interview.monetary.exception.BillChangeException;
import com.adp.interview.monetary.model.BillChangeRequest;
import com.adp.interview.monetary.model.BillChangeResponse;
import com.adp.interview.monetary.model.CoinsModel;
import com.adp.interview.monetary.component.BillChangeComponent;
import com.adp.interview.monetary.validator.BillChangeValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

@RestController
public class BillChangeController {

    private BillChangeComponent billChangeService;
    private BillChangeValidator billChangeValidator;

    public BillChangeController(BillChangeComponent billChangeService,
                                BillChangeValidator billChangeValidator) {
        this.billChangeService = billChangeService;
        this.billChangeValidator = billChangeValidator;
    }

    @PostMapping(path= "/change")
    public ResponseEntity<BillChangeResponse> calculateChange (@RequestBody BillChangeRequest billChangeRequest,
                                                               HttpServletRequest request) throws BillChangeException {
        BillChangeResponse billChangeResponse = new BillChangeResponse();
        if (!billChangeValidator.validate(billChangeRequest.getBill())){
            throw new BillChangeException(MessageFormat.format("Invalid bill: {0}", billChangeRequest.getBill()));
        }

        CoinsModel coins = (CoinsModel) request.getSession()
                .getAttribute("COINS_SESSION");

        if (coins == null ){
            coins = billChangeService.buildCoins();
                request.getSession().setAttribute("COINS_SESSION", coins);
        } else {
            coins = billChangeService.getChange(coins, billChangeRequest.getBill());
        }

        billChangeResponse.setCoins(coins);
        return ResponseEntity.status(HttpStatus.OK)
                .body(billChangeResponse);
    }

    @PutMapping("/reset")
    public ResponseEntity<?> reset(HttpServletRequest request){
        //invalidate the session , this will clear the data from configured database (Mongo)
        request.getSession().invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
