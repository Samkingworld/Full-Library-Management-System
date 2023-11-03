package com.groupproject.libraryManagementSystem.service.loanBookService;

import com.groupproject.libraryManagementSystem.dto.loanDTO.LoanBookRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.PayFineRequest;
import com.groupproject.libraryManagementSystem.dto.loanDTO.ReturnBookRequest;

import java.util.Map;

public interface LoanBookService {

    Map<String, Object> loanBook (LoanBookRequest loanBookRequest);

    Map<String, Object> returnBook (ReturnBookRequest returnBook);

    Map<String, Object> payFine (PayFineRequest payFineRequest);
}
