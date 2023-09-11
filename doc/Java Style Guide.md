### 1. Naming

#### 1.1 Variable
- Single-letter variable name is allowed in lambda expressions and functions <= 5 lines

    ```
    Flux.of(products)
        .filter(p -> p.isOnSale())
        .toList();
    ```
#### 1.2 Function
- Unit tests start with `test` or `testWhen`

    ```
    public class FailtoAddProductTests {
        @Test
        public void testWhenTitleIsEmpty() { }
    }
    ```
- Unimplemented functions have curly brackets on the same line

    ```
    public void update(Product p) { }
    ```

#### 1.3 Service
- Services end with `Service`

    ```
    public class AuctionService { }
    ```
- Identity services end with `Manager`
    ```
    public class AccountManager { }
    ```
#### 1.4 Repository

- Repositories end with `Repository`
    ```
    public class AuctionRepository { }
    ```
#### 1.5 Test

- Test classes end with `Tests`
    
    ```
    public class AuctionServiceTests() { }
    ```
- Verb is allowed in the name of test classes
    
    ```
    public class FailToPutForAuctionTests() { }
    ```

### 2. Commenting
#### 2.1 Spacing
- A comment starts 2 spaces after `//`

    ```
    // X
    ```
### 3. Formatting
#### 3.1 If Statements
- Curly brackets can be dropped for single-line if blocks

    ```
    if (product.isOnSale()) 
        result.add(product);
    ```
- Line break can be dropped for short single-line if statements

    ```
    if (product == null) return;
    ```
#### 3.2 For Loop
- Curly brackets can be dropped for single-line for loops

    ```
    for (ProductDTO p : products) 
        service.putForAuction(p);
    ```
- Line break can be dropped for short single-line for loops

    ```
    for (Product p : product) result.add(p);
    ```

