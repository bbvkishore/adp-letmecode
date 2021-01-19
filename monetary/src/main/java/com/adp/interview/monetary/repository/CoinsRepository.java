package com.adp.interview.monetary.repository;

import com.adp.interview.monetary.domain.Coins;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoinsRepository extends MongoRepository<Coins, String> {
}
