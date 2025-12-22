package com.nihilent.BankingApplication.NihilentBank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.Roles;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.serviceimpl.CustomerServiceImpl;

//@ExtendWith(MockitoExtension.class)
class NihilentBankApplicationTests {


//    demo test cae
public int a=10;
}
