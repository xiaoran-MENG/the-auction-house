package comp3350.group9.theauctionhouse.business.accounts;

import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;

public class TrustFactorManager {
    private final QueryEngine query;

    private static volatile TrustFactorManager o = null;

    public TrustFactorManager(QueryEngine query) {
        this.query = query;
    }

    public static synchronized TrustFactorManager get() {
        if (o == null) {
            synchronized (TrustFactorManager.class) {
                if (o == null) o = new TrustFactorManager(HSQLDBFactory.get());
            }
        }

        return o;
    }

    public boolean addRating(TrustFactor trust) {
        return this.query.trustFactors().add(trust);
    }


    public TrustFactor getByUserId(String id) { return this.query.trustFactors().findByUserId(id); }

    public Flux<TrustFactor> getAll() {
        return Flux.of(this.query.trustFactors().findAll());
    }
}
