package com.nihilent.BankingApplication.NihilentBank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.nihilent.bankingApplication.dto.BankAccountDto;
import com.nihilent.bankingApplication.dto.CustomerDto;
import com.nihilent.bankingApplication.entity.AccountStatus;
import com.nihilent.bankingApplication.entity.AccountType;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.Customer;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.service.BankAccountService;
import com.nihilent.bankingApplication.serviceImpl.BankAccountServiceImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class NihilentBankApplicationTests {

//	@Mock
//	private BankAccountRepository accountRepository;

//	@InjectMocks
//	private BankAccountService accountService = new BankAccountServiceImpl();
	
    @Mock
    private BankAccountRepository accountRepository;

    @InjectMocks
    private BankAccountServiceImpl accountService;

//	public List<BankAccountDto> showAllAcountsDetails() throws NihilentBankException;

//	@Test
//	public void showAllAcountsDetailsTest() throws NihilentBankException {
//
////		Integer pageNumber = 0, pageSize = 4;
////
////		Pageable pageable = PageRequest.of(pageNumber, pageSize);
//
//		List<BankAccount> listOfAccount = new ArrayList<>();
//
//		BankAccount bankAccount = new BankAccount();
//		bankAccount.setAccountNumber(12345678l);
//		bankAccount.setAccountStatus(AccountStatus.ACTIVE);
//		bankAccount.setAccountType(AccountType.CURRENT);
//		bankAccount.setAdharCard("782413123456");
//		bankAccount.setBalance(2000.00);
//		bankAccount.setBankName("ICICI Bank");
////		bankAccount.setDateOfBirth(LocalDate.of(07, 30, 2000));
//		bankAccount.setIfscCode("GHSGG123");
//		bankAccount.setPanCard("GHFGSH9892");
//		bankAccount.setOpeningDate(LocalDate.now());
//
//		Customer customer = new Customer();
//
//		customer.setAddress("nagpur");
//		customer.setCustomerId("1");
//		customer.setEmailId("anikt@gmail.com");
//		customer.setGender("Male");
//		customer.setMobileNumber(9823423429l);
//		customer.setName("Aniket");
//
//		bankAccount.setCustomer(customer);
//
//		listOfAccount.add(bankAccount);
//
////		Page<Medicine> page = new PageImpl<>(listOfMedicine);
//
//		Mockito.when(accountRepository.findByMobileNumbers(9823423429l)).thenReturn(listOfAccount);
//
//		Assertions.assertDoesNotThrow(() -> accountService.getAccountDetail(12345678l));
//
//	}
	
	
	
	
	
	
	
	
	
//	
//	@ExtendWith(MockitoExtension.class)
//	public class BankAccountServiceTest {



	    @Test
	    public void getAccountDetailTest() throws NihilentBankException {
	        // Arrange
	        List<BankAccount> listOfAccounts = new ArrayList<>();

	        BankAccount bankAccount = new BankAccount();
	        bankAccount.setAccountNumber(12345678L);
	        bankAccount.setAccountStatus(AccountStatus.ACTIVE);
	        bankAccount.setAccountType(AccountType.CURRENT);
	        bankAccount.setAdharCard("782413123456");
	        bankAccount.setBalance(2000.00);
	        bankAccount.setBankName("ICICI Bank");
	        bankAccount.setIfscCode("GHSGG123");
	        bankAccount.setPanCard("GHFGSH9892");
	        bankAccount.setOpeningDate(LocalDate.now());

	        Customer customer = new Customer();
	        customer.setAddress("Nagpur");
	        customer.setCustomerId("1");
	        customer.setEmailId("anikt@gmail.com");
	        customer.setGender("Male");
	        customer.setMobileNumber(9823423429L);
	        customer.setName("Aniket");

	        bankAccount.setCustomer(customer);

	        listOfAccounts.add(bankAccount);

	        Mockito.when(accountRepository.findByMobileNumber(9823423429L)).thenReturn(listOfAccounts);

	        // Act & Assert
//	        Assertions.assertDoesNotThrow(() -> {
//	            BankAccount dto = accountService.getAccountDetail(12345678L);
//	            Assertions.assertNotNull(dto);
//	            Assertions.assertEquals(12345678L, dto.getAccountNumber());
//	        });
//	    }
	    }


}
