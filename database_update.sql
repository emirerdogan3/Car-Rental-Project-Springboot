-- Employee Payment System - Database Update Script

-- Create EmployeePayments table
CREATE TABLE EmployeePayments (
    employeePaymentID INT IDENTITY(1,1) PRIMARY KEY,
    employeeID INT NOT NULL,
    accountID INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    paymentDate DATETIME2 NOT NULL DEFAULT GETDATE(),
    description NVARCHAR(255),
    paidByUserID INT NOT NULL,
    
    -- Foreign Keys
    CONSTRAINT FK_EmployeePayments_Employee 
        FOREIGN KEY (employeeID) REFERENCES Employees(employeeID) 
        ON DELETE CASCADE,
    CONSTRAINT FK_EmployeePayments_MoneyAccount 
        FOREIGN KEY (accountID) REFERENCES MoneyAccount(accountID),
    CONSTRAINT FK_EmployeePayments_PaidBy 
        FOREIGN KEY (paidByUserID) REFERENCES Users(userID)
);

-- Create index for better performance
CREATE INDEX IX_EmployeePayments_Employee ON EmployeePayments(employeeID);
CREATE INDEX IX_EmployeePayments_PaymentDate ON EmployeePayments(paymentDate);
CREATE INDEX IX_EmployeePayments_PaidBy ON EmployeePayments(paidByUserID); 