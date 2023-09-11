package comp3350.group9.theauctionhouse.persistence;

import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.AuctionTagQueriable;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.application.ProductQueriable;
import comp3350.group9.theauctionhouse.core.application.Queriable;
import comp3350.group9.theauctionhouse.core.application.ReportQueriable;
import comp3350.group9.theauctionhouse.core.application.TrustFactorQueriable;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.persistence.executor.*;

public class HSQLDBQueryEngine implements QueryEngine {
    private final String path;
    private final AuctionQueryExecutor auctionQueryExecutor;
    private final ProductQueryExecutor productQueryExecutor;
    private final BidQueryExecutor bidQueryExecutor;
    private final UserQueryExecutor userQueryExecutor;
    private final ReportQueryExecutor reportQueryExecutor;
    private final TrustFactorQueryExecutor trustFactorQueryExecutor;
    private final AuctionTagQueryExecutor auctionTagQueryExecutor;

    public HSQLDBQueryEngine(String path){
        this.path = path;
        this.productQueryExecutor = new ProductQueryExecutor(path);
        this.bidQueryExecutor = new BidQueryExecutor(path);
        this.userQueryExecutor = new UserQueryExecutor(path);
        this.reportQueryExecutor = new ReportQueryExecutor(path);
        this.trustFactorQueryExecutor = new TrustFactorQueryExecutor(path);
        this.auctionTagQueryExecutor = new AuctionTagQueryExecutor(path);

        this.auctionQueryExecutor = new AuctionQueryExecutor(path,bidQueryExecutor,auctionTagQueryExecutor);
    }

    @Override
    public AuctionQueriable auctions() {
        return auctionQueryExecutor;
    }

    @Override
    public ProductQueriable products() {
        return productQueryExecutor;
    }

    @Override
    public BidQueriable bids() {
        return bidQueryExecutor;
    }

    @Override
    public UserQueriable users() {
        return userQueryExecutor;
    }

    @Override
    public ReportQueriable reports() {
        return reportQueryExecutor;
    }

    @Override
    public TrustFactorQueriable trustFactors() {
        return trustFactorQueryExecutor;
    }

    @Override
    public AuctionTagQueriable auctionTags() {
        return auctionTagQueryExecutor;
    }
}
