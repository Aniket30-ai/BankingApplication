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
• Fund Transfers
• Loan Module            
• Transaction History & Account Statements
• Bill Payments
• Admin Account management
• Admin Transaction Monitoring
• Admin Loan Management


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


