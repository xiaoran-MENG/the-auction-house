package comp3350.group9.theauctionhouse.business.catalog;

import java.util.List;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.business.common.functional.Mono;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.Auction;

public class Catalog {
    private final QueryEngine query;

    public Catalog(QueryEngine query) {
        this.query = query;
    }

    public static Catalog of() {
        return new Catalog(HSQLDBFactory.get());
    }

    public Flux<AuctionCatalogDTO> getAll() {
        return Flux.of(query.auctions().findAll()).map(AuctionCatalogDTO::of);
    }

    public Mono<AuctionCatalogDTO> getById(String id){
        return Mono.of(query.auctions().findById(id)).map(AuctionCatalogDTO::of);
    }

    public Flux<AuctionCatalogDTO> getRecentAuctionsForSeller(String sellerId, int limit){
        List<Auction> recentAuctions = query.auctions().findBySellerIdOrderedByDateCreated(sellerId, limit);
        return Flux.of(recentAuctions).map(AuctionCatalogDTO::of);
    }

    public Flux<AuctionCatalogDTO> getByTags(List<String> tagsToSearch){
        return Flux.of(query.auctions().findByTags(tagsToSearch)).map(AuctionCatalogDTO::of);
    }
}
