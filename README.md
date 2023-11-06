# Full-Library-Management-System
Built with Java and SpringBoot. This is a complete Library Management System


## **Features enabled for USERS**

1. User Registration
2. User recieves OTP to verify email and other details
3. OTP Service
4. User_EMail OTP Validation through user verify endpoint 
5. User can Change password (OTP will be sent to user's email)
7. If otp is not sent on first attempt of user verification due to SMTP server network, or OTP Expire before user validation, user can get OTP on attept to signing or resend OTP     
   through "resend OTP endpoint"
9. User can Sign-in
10. User can update some of his/her details without contacting ADMIN
11. See All Books
12. Search book base on author
13. Search book base on ISBN
14. Reserve Books if not available (not more than two books)
15. A CRON Services/Scheduler that automatically check database every 30min for available books and send email to users who reserved such books.
16. A Scheduler that automatically check database and delete any reservation for books made where notification status is YES and notification sent to user's email more than 2hrs   
    without user coming to borrow the book.

## **Features enabled for ADMIN**
**NOTE: ** ADMIN can use all the features accessible to USERS

**OTHER FUNCTIONS ADMIN CAN PERFORM**
1. Add new Books
2. Update Books
3. Delete Books
4. Borrow Books out to users
5. Reserve Books for Users over the counter
6. Record books return by users
7. Generate User's Fine
8. Record fine paid by users
9. etc....

## **SYSTEM Features THAT PERFORMS AUTOMATIC TASK WITHOUT USERS AND ADMIN INTERERENCE/COMMAND**
1. A CRON Services/Scheduler that automatically check database every 30min for available books and send email to users who reserved such books.
2. A Scheduler that automatically check database and delete any reservation for books made where notification status is YES and notification sent to user's email more than 2hrs   
    without user coming to borrow the book.
3. A Scheduler that automatically check database every 4hours (6 times in a day) for any borrowed book that is due for return the next day and automatically send email to such users 
   for REMINDER FOR RETURN.


DOWNLOAD THE PDF ATTACHED IN THE ROOT FOLDER FOR FULL API DOCUMENTATION!
OR
VISTIT THE LINK BELOW for the documentation or click this link [DOCUMENTATION IN PDF](https://github.com/Samkingworld/Full-Library-Management-System/blob/main/LMS%20Endpoints%20and%20Payloads%20structure.pdf):
https://github.com/Samkingworld/Full-Library-Management-System/blob/main/LMS%20Endpoints%20and%20Payloads%20structure.pdf
 
