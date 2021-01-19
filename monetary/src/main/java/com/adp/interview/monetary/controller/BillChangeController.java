package com.adp.interview.monetary.controller;

import com.adp.interview.monetary.exception.BillChangeException;
import com.adp.interview.monetary.model.BillChangeRequest;
import com.adp.interview.monetary.model.BillChangeResponse;
import com.adp.interview.monetary.service.BillChangeService;
import com.adp.interview.monetary.validator.BillChangeValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
public class BillChangeController {

    private BillChangeService billChangeService;
    private BillChangeValidator billChangeValidator;

    public BillChangeController(BillChangeService billChangeService,
                                BillChangeValidator billChangeValidator) {
        this.billChangeService = billChangeService;
        this.billChangeValidator = billChangeValidator;
    }

    @PostMapping("/change")
    public ResponseEntity<BillChangeResponse> calculateChange (@RequestBody BillChangeRequest billChangeRequest) throws BillChangeException {
        BillChangeResponse billChangeResponse;
        if (!billChangeValidator.validate(billChangeRequest.getBill())){
            throw new BillChangeException(MessageFormat.format("Invalid bill: {0}", billChangeRequest.getBill()));
        }

        billChangeResponse = billChangeService.getChange(billChangeRequest.getBill());
        return ResponseEntity.status(HttpStatus.OK)
                            .body(billChangeResponse);
    }

    @GetMapping("/status")
    public ResponseEntity<BillChangeResponse> getStatus(){
        BillChangeResponse billChangeResponse = billChangeService.getStatus();
        return ResponseEntity.status(HttpStatus.OK)
                            .body(billChangeResponse);
    }

    @PutMapping("/reset")
    public ResponseEntity<?> reset(){
        billChangeService.reset();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
