# Banking System

Banking Application is designed to streamline core banking operations for both customers and administrators. It 
provides secure and real-time access to essential banking services, including user account management, fund transfers, 
transaction history, loan processing, bulk transaction uploads, and account statement generation. 

## Table of Contents
- Features
- Database Schema
- Installation

## Features

• Customer Account Management 
   ➢ Customer Registration & Authentication: - Customer can create accounts and log in securely. 
   ➢ Profile Management: - Customers can view and update their personal information, including 
                         contact details, and address. 
   ➢ Bank Account Details: - Customers can view their bank account information, such as account 
                        number, account type, branch, balance, UPI ID and QR Code. 
                        
• Fund Transfers 
   ➢ Domestic Payments: - 
     • Fast payment: - Instant transfers up to 10,000.  
     • NEFT: Transfer over 10,000. 
     • RTGS: High value transfers above 2 lakhs. 
   ➢ UPI (Unified payments Interface): - 
     • Customers can send money directly using UPI ID. 
     • Transfers can also be made by uploading or scanning QR code. 
     • The daily UPI limit is 50,000.  
     
• Loan Module 
   ➢ Apply for Loans: - 
     • Home Loan    
     • Personal Loan 
     • Education Loan 
   ➢ Track Loan Status: - Customer can track the status of their loan application in real-time, from 
                           submission to approval. 
                           
• Transaction History & Account Statements 
   ➢ View Transaction Details: - Customer can view a detailed list of all their transactions details. 
   ➢ Download Account Statements: - Customer can download their account statement in PDF format. 
   
• Bill Payments 
   ➢ Mobile Recharge: - Allows users to recharge prepaid mobile numbers instantly for all major telecom 
                        operators such as Vi, Airtel and Jio. 
   ➢ DTH Recharge: - Users can recharge their DTH connections quickly using their account balance. 
   ➢ Electricity Bill payment: - Enables users to pay their electricity bills online by selecting their service 
                               provider and entering customer details. 
• Admin Account management 
   ➢ Approve or reject new account creation requests. 
   ➢ Create new user accounts. 
   ➢ View detailed information for each account. 
   ➢ Delete or deactivate account.  
• Admin Transaction Monitoring 
   ➢ View all transactions across the system. 
   ➢ Upload bulk transactions through file uploads. 
• Admin Loan Management 
   ➢ View and track incoming loan requests. 
   ➢ Approve or reject loan application.

## Database Schema

**Tables:**
- customer
- bank_account
- bank_account_request
- digital_bank_account
- beneficiary_account
- loan
- transaction
- audit_log

## Installation
 1. Clone the repository
   ```bash
   git clone https://github.com/username/digital-banking.git

 2. Open MySQL and create a database
    CREATE DATABASE digital_banking;

 3. Import the SQL script
      USE digital_banking;
      SOURCE Table Script NihilentBank.sql;


