package com.nihilent.BankingApplication.NihilentBank.serviceTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.CreditCard;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CreditCardRepository;
import com.nihilent.bank.serviceimpl.CreditCardServiceImpl;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {



    @Mock
    private CreditCardRepository repository;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    private CreditCard creditCard;

    @BeforeEach
    void setUp() {


        // Inject @Value fields
        ReflectionTestUtils.setField(creditCardService, "requestSend", "Request Sent");
        ReflectionTestUtils.setField(creditCardService, "invalidId", "Invalid ID");
        ReflectionTestUtils.setField(creditCardService, "creditCardNotFound", "Request Not Found");
        ReflectionTestUtils.setField(creditCardService, "requestApproved", "Approved: ");

        // Mock entity
        BankAccount account = new BankAccount();
        account.setAccountNumber(12345L);

        creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setBankAccount(account);
        creditCard.setStatus(CreditCard.Status.PENDING);
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST applyForCard() — SUCCESS
    // ----------------------------------------------------------------------
    @Test
    void testApplyForCard_Success() throws Exception {
        when(repository.save(any(CreditCard.class))).thenReturn(creditCard);

        String result = creditCardService.applyForCard(12345L);

        assertEquals("Request Sent", result);
        verify(repository, times(1)).save(any());
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST getRequestByUserId()
    // ----------------------------------------------------------------------
    @Test
    void testGetRequestByUserId() throws Exception {
        when(repository.findCreditCardByAccountNumber(12345L))
                .thenReturn(List.of(creditCard));

        Optional<List<CreditCard>> result = creditCardService.getRequestByUserId(12345L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST showCreditCard() — SUCCESS
    // ----------------------------------------------------------------------
    @Test
    void testShowCreditCard_Success() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(creditCard));

        CreditCard result = creditCardService.showCreditCard(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST showCreditCard() — INVALID ID
    // ----------------------------------------------------------------------
    @Test
    void testShowCreditCard_InvalidId() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                creditCardService.showCreditCard(1L)
        );

        assertEquals("Invalid ID", ex.getMessage());
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST getPendingRequests() — SUCCESS
    // ----------------------------------------------------------------------
    @Test
    void testGetPendingRequests() throws Exception {

        when(repository.findByStatus(CreditCard.Status.PENDING))
                .thenReturn(List.of(creditCard));

        List<CreditCard> result = creditCardService.getPendingRequests();

        assertEquals(1, result.size());
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST approveRequest() — REQUEST EXISTS
    // ----------------------------------------------------------------------
    @Test
    void testApproveRequest_Success() throws Exception {

        when(repository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(repository.save(any(CreditCard.class))).thenReturn(creditCard);

        String result = creditCardService.approveRequest(1L);

        assertTrue(result.startsWith("Approved: "));
        assertNotNull(creditCard.getCardNumber());
        assertNotNull(creditCard.getCardExpiry());
        assertEquals(CreditCard.Status.APPROVED, creditCard.getStatus());

        verify(repository, times(1)).save(creditCard);
    }

    // ----------------------------------------------------------------------
    // 🔹 TEST approveRequest() — NOT FOUND
    // ----------------------------------------------------------------------
    @Test
    void testApproveRequest_NotFound() throws Exception {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        String result = creditCardService.approveRequest(1L);

        assertEquals("Request Not Found", result);
        verify(repository, never()).save(any());
    }


}

