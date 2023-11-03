APPLICATION LAYER ARCHITECTURE

The Application Layer in the Library Management System (LMS) comprises controllers, services, and Data Transfer Objects (DTOs) that collectively handle various aspects of the system's functionality.
 Controller Architecture

 Admin Controller

 Admin endpoints:

 @RestController
 RequestMapping/admin
 This gives first access to the admin upon opening the app
 @RequestMapping("/api/v1/admin")

 public class AdminController {

     @Autowired
     UserService userService;
     @Autowired
     LoanBookService loanBookService;
     @Autowired
     CatalogService catalogService;
     @Autowired
     ReservedBookService reservedBookService;



 //    *************************** USERS **********************************

 /Get/Users
     Admin can access all users. Admin can also sort and search users at any page and using any id. Each webpage would have numbers 0-10
     that would allow admin to switch pages by clicking on the number of any page they want to see.

     @GetMapping("/users/all")
     public ResponseEntity<Page<User>> getAllUsers(
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(defaultValue = "id") String sortField
     ){
         Page<User> users = userService.getAllUsers(page, size, sortField);
         return ResponseEntity.ok(users);
     }

/Put/updateRole
     In the service class, we cached this so that user can easily access without interacting with database directly.
     Also, admin will be able to update user roles. First we check if the user is existing and if there is a role already assigned, then we go ahead to write the logic for updating the role.

     @PutMapping("/update/role/{email}")
     public Map<String, Object> updateRole(@PathVariable String email, @RequestBody UpdateRoleRequest updateRole){
         Map<String, Object> userToUpdate = userService.updateRole(email, updateRole.getRole());
         return userToUpdate;
     }

/Delete/memberShipNo/
     This endpoint enables admin to delete user using their membership id.

     @DeleteMapping("/memberShipNo/{membershipNo}")
     public String deleteUserByUserId(@PathVariable String membershipNo){return userService.deleteByMembershipNo(membershipNo);}
/Delete/delete/email/
     This endpoint allows admin to delete user to
     @DeleteMapping("/email/{email}")
     public String deleteUserByEmail(@PathVariable String email){return userService.deleteUserByEmail(email);}
Post/loan-book/

 This endpoint allows users to borrow or loan books from the library.

     //    *************************** loanBooks **********************************
Post/loan-book/
     @PostMapping("/loanBook-management/loan-book")
     Map<String, Object> loanBook (@RequestBody @Valid LoanBookRequest loanBookRequest){
         return loanBookService.loanBook(loanBookRequest);
     }

     Post/return-book/
     This endpoint allows users to return books which they borrowed

     @PostMapping("/loanBook-management/return-book")
     Map<String, Object> returnBook (@RequestBody @Valid ReturnBookRequest returnBook){
         return loanBookService.returnBook(returnBook);
     }
Post/pay-fine/
This endpoint allows user to pay fine for delaying books beyond the scheduled period.
The system is able to calculate the amount of fine they are going to pay based on the period of delay.

     @PostMapping("/loanBook-management/pay-fine")
     Map<String, Object> payFine (PayFineRequest payFineRequest){
         return loanBookService.payFine(payFineRequest);
     }


     //    *************************** CATALOGS **********************************
/Post/addBook/
This endpoint allows admin to add a book to the book catalogue. The system first checks if the book was not already in the catalogue. If it wasn't the new book can be added.
     @PostMapping("/catalog/book/add")
     public Object addBook(@RequestBody @Valid AddBookRequest book){return catalogService.addBook(book);}

     @PutMapping("/catalog/update/isbn/{book_ISBN}")
     public Object updateBookByIsbn(@PathVariable("book_ISBN") String bookIsbn, @RequestBody UpdateBookRequest bookRequest)
     {
         return catalogService.updateBookByIsbn(bookIsbn, bookRequest);
     }
/Put/updateBookById/
Using this endpoint, admin can check if a book exists by the id. Iff it exists, admin would be able to update the book. If it does not exist, then they won't be able to.
     @PutMapping("/catalog/update/id/{book_ID}")
     public Object updateBookById(@PathVariable("book_ID") Long bookId, @RequestBody UpdateBookRequest bookRequest)
     {
         return catalogService.updateBookById(bookId, bookRequest);
     }
/Delete/deleteBookByIsbn
This endpoint allows users to delete books using their ISBN
     @DeleteMapping("/catalog/delete/isbn/{book_ISBN}")
     public String deleteBookByIsbn(@PathVariable("book_ISBN") String bookIsbn){return catalogService.deleteBookByIsbn(bookIsbn);}
/Delete/deleteBookById/
Admin can delete book by entering the unique identifier of the book.
     @DeleteMapping("/catalog/delete/id/{book_ID}")
     public String deleteBookById(@PathVariable("book_ID") Long bookId){return catalogService.deleteBookById(bookId); }


     //    *************************** BOOK RESERVATION **********************************
/Post/reserveBook/
Using this endpoint a user can reserve a book for about two hours after the book is available to reserve
     @PostMapping("/book-reservation/reserve")
     public Object reserveBook (@RequestBody @Valid ReservedBookRequest request){return reservedBookService.reserveBook(request);}

/Delete/deleteReservedBook
The system deletes reserved Book if user does not come to take it at the appointed time

     @DeleteMapping("/book-reservation/delete/{reservationID}")
     public Object deleteReservedBook(@PathVariable("reservationID") Long reservationID){return reservedBookService.deleteReservedBook(reservationID);}

 }

 ProductService
 Methods:
 getAllProducts():

 Description: Retrieve a list of all products.
 Functionality: Fetches and returns a paginated list of all available products.
 getProductById(Long id):

 Description: Retrieve product details by ID.
 Functionality: Retrieves and returns detailed information about a specific product based on its unique identifier.
 getProductByBrandName(String brandName):

 Description: Retrieve list of products by a brand.
 Functionality: Retrieves and returns a paginated list of products based on the unique brand name.
 createProduct(ProductDTO productDTO):

 Description: Create a new product.
 Functionality: Accepts product data and creates a new product in the system, returning the details of the newly created product.
 updateProduct(Long id, ProductDTO productDTO):

 Description: Update an existing product.
 Functionality: Updates the attributes of an existing product based on its unique identifier and the provided product data. Returns the updated product information.
 deleteProduct(Long id):

 Description: Delete a product.
 Functionality: Removes a product from the system based on its unique identifier\


