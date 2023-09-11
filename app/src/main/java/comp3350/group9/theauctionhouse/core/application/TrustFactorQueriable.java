package comp3350.group9.theauctionhouse.core.application;


import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
public interface TrustFactorQueriable extends Queriable<TrustFactor> {
    TrustFactor findByUserId(String id);
}