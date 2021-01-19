package com.adp.interview.monetary.component;

import com.adp.interview.monetary.model.CoinsModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BillChangeComponent.class)
@TestPropertySource("classpath:application-test.properties")
class BillChangeComponentTest {

    @Autowired
    private BillChangeComponent billChangeComponent;

    @Test
    public void testGetChange(){
        CoinsModel coinsModel = billChangeComponent.buildCoins();
        billChangeComponent.getChange(coinsModel, 1);
        assertEquals(6, coinsModel.getQuarters());
    }


    @Test
    public void testBuildCoinModel(){
        CoinsModel coinsModel = billChangeComponent.buildCoins();
        assertTrue(coinsModel != null);
        assertEquals(String.valueOf(coinsModel.getDimes()),
                billChangeComponent.getQuantityValue());
    }


}