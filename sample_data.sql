-- Car Rental System - Sample Data Insert Script
-- Bu dosyayı çalıştırmadan önce database.sql dosyasının çalıştırılmış olması gerekir
-- F5 ile çalıştırabilirsiniz

USE [CarRentalDB];
GO

-- 1. Roles tablosu - Önce roller eklenir
INSERT INTO [dbo].[Roles] ([RoleName]) VALUES 
('ADMIN'),
('MANAGER'), 
('EMPLOYEE'),
('CUSTOMER');
GO

-- 2. Country tablosu
INSERT INTO [dbo].[Country] ([CountryName]) VALUES 
('Turkey'),
('Germany'),
('France'),
('Italy'),
('Spain');
GO

-- 3. Cities tablosu
INSERT INTO [dbo].[Cities] ([CityName], [CountryID]) VALUES 
('Istanbul', 1),
('Ankara', 1),
('Izmir', 1),
('Antalya', 1),
('Bursa', 1),
('Berlin', 2),
('Munich', 2),
('Paris', 3),
('Rome', 4),
('Madrid', 5);
GO

-- 4. County tablosu
INSERT INTO [dbo].[County] ([CountyName], [CityID]) VALUES 
('Kadikoy', 1),
('Besiktas', 1),
('Sisli', 1),
('Cankaya', 2),
('Kecioren', 2),
('Konak', 3),
('Bornova', 3),
('Muratpasa', 4),
('Konyaalti', 4),
('Osmangazi', 5);
GO

-- 5. Users tablosu - Test için basit şifreler
INSERT INTO [dbo].[Users] ([Username], [PasswordHash], [Email], [Phone], [RoleID], [enabled]) VALUES 
('admin', 'admin123', 'admin@rentacar.com', '+905551234567', 1, 1),
('manager1', 'manager123', 'manager1@rentacar.com', '+905551234568', 2, 1),
('manager2', 'manager123', 'manager2@rentacar.com', '+905551234569', 2, 1),
('employee1', 'employee123', 'employee1@rentacar.com', '+905551234570', 3, 1),
('employee2', 'employee123', 'employee2@rentacar.com', '+905551234571', 3, 1),
('employee3', 'employee123', 'employee3@rentacar.com', '+905551234572', 3, 1),
('customer1', 'customer123', 'customer1@gmail.com', '+905559876543', 4, 1),
('customer2', 'customer123', 'customer2@gmail.com', '+905559876544', 4, 1),
('customer3', 'customer123', 'customer3@gmail.com', '+905559876545', 4, 1),
('customer4', 'customer123', 'customer4@gmail.com', '+905559876546', 4, 1),
('customer5', 'customer123', 'customer5@gmail.com', '+905559876547', 4, 1);
GO

-- 6. Manager tablosu
INSERT INTO [dbo].[Manager] ([UserID], [FirstName], [LastName]) VALUES 
(2, 'Mehmet', 'Yilmaz'),
(3, 'Ayse', 'Kaya');
GO

-- 7. Branch tablosu
INSERT INTO [dbo].[Branch] ([UserID], [BranchName], [CountryID], [CityID], [CountyID], [phoneNumber]) VALUES 
(2, 'Istanbul Kadikoy Branch', 1, 1, 1, '+902164567890'),
(3, 'Ankara Cankaya Branch', 1, 2, 4, '+903124567890'),
(2, 'Izmir Konak Branch', 1, 3, 6, '+902324567890'),
(3, 'Antalya Muratpasa Branch', 1, 4, 8, '+902424567890');
GO

-- 8. BranchManagers tablosu
INSERT INTO [dbo].[BranchManagers] ([ManagerID], [BranchID]) VALUES 
(1, 1),
(1, 3),
(2, 2),
(2, 4);
GO

-- 9. MoneyAccount tablosu
INSERT INTO [dbo].[MoneyAccount] ([BranchID], [MoneyBalance]) VALUES 
(1, 150000.00),
(2, 125000.00),
(3, 95000.00),
(4, 180000.00);
GO

-- 10. Employees tablosu
INSERT INTO [dbo].[Employees] ([UserID], [FirstName], [LastName], [Salary], [PositionTitle], [BranchID]) VALUES 
(4, 'Ali', 'Demir', 8500.00, 'Customer Service Representative', 1),
(5, 'Fatma', 'Ozturk', 9200.00, 'Rental Agent', 2),
(6, 'Emre', 'Arslan', 8800.00, 'Customer Service Representative', 3);
GO

-- 11. Customers tablosu
INSERT INTO [dbo].[Customers] ([UserID], [FirstName], [LastName], [LicenseNumber], [Address]) VALUES 
(7, 'Murat', 'Celik', 'DL123456789', 'Kadikoy, Istanbul'),
(8, 'Zeynep', 'Akman', 'DL234567890', 'Cankaya, Ankara'),
(9, 'Burak', 'Yildiz', 'DL345678901', 'Konak, Izmir'),
(10, 'Selin', 'Kaplan', 'DL456789012', 'Muratpasa, Antalya'),
(11, 'Cem', 'Polat', 'DL567890123', 'Besiktas, Istanbul');
GO

-- 12. CarBrand tablosu
INSERT INTO [dbo].[CarBrand] ([BrandName]) VALUES 
('Toyota'),
('BMW'),
('Mercedes-Benz'),
('Volkswagen'),
('Ford'),
('Hyundai'),
('Renault'),
('Fiat'),
('Audi'),
('Honda');
GO

-- 13. CarBrandModel tablosu
INSERT INTO [dbo].[CarBrandModel] ([ModelName], [BrandID]) VALUES 
('Corolla', 1),
('Camry', 1),
('RAV4', 1),
('3 Series', 2),
('5 Series', 2),
('X3', 2),
('C-Class', 3),
('E-Class', 3),
('GLC', 3),
('Passat', 4),
('Golf', 4),
('Tiguan', 4),
('Focus', 5),
('Mondeo', 5),
('Kuga', 5),
('i20', 6),
('Tucson', 6),
('Santa Fe', 6),
('Clio', 7),
('Megane', 7),
('Captur', 7),
('Punto', 8),
('500', 8),
('Tipo', 8),
('A3', 9),
('A4', 9),
('Q3', 9),
('Civic', 10),
('Accord', 10),
('CR-V', 10);
GO

-- 14. CarCategories tablosu
INSERT INTO [dbo].[CarCategories] ([CategoryName]) VALUES 
('Economy'),
('Compact'),
('Mid-size'),
('Full-size'),
('Luxury'),
('SUV'),
('Premium'),
('Sports');
GO

-- 15. Colors tablosu
INSERT INTO [dbo].[Colors] ([ColorName]) VALUES 
('White'),
('Black'),
('Silver'),
('Red'),
('Blue'),
('Grey'),
('Brown'),
('Green'),
('Yellow'),
('Orange');
GO

-- 16. FuelType tablosu
INSERT INTO [dbo].[FuelType] ([FuelTypeName]) VALUES 
('Gasoline'),
('Diesel'),
('Hybrid'),
('Electric'),
('LPG');
GO

-- 17. Cars tablosu
INSERT INTO [dbo].[Cars] ([BrandID], [ModelID], [CategoryID], [PlateNumber], [Kilometer], [PricePerDay], [FuelTypeID], [Status], [ModelYear], [ColorID], [BranchID]) VALUES 
(1, 1, 1, '34ABC123', 45000, 120.00, 1, 'Available', 2022, 1, 1),
(1, 2, 3, '06DEF456', 32000, 180.00, 1, 'Available', 2023, 2, 2),
(2, 4, 5, '35GHI789', 28000, 350.00, 1, 'Available', 2023, 3, 1),
(2, 6, 6, '07JKL012', 41000, 420.00, 2, 'Rented', 2022, 4, 2),
(3, 7, 5, '34MNO345', 22000, 380.00, 2, 'Available', 2024, 1, 1),
(4, 10, 3, '35PQR678', 36000, 200.00, 2, 'Available', 2023, 5, 3),
(5, 13, 2, '07STU901', 48000, 160.00, 1, 'Available', 2022, 6, 2),
(6, 16, 1, '34VWX234', 51000, 110.00, 1, 'Available', 2021, 7, 1),
(7, 19, 2, '35YZA567', 29000, 140.00, 1, 'Maintenance', 2023, 8, 3),
(8, 22, 1, '07BCD890', 38000, 130.00, 1, 'Available', 2022, 9, 4),
(9, 25, 5, '34EFG123', 15000, 450.00, 1, 'Available', 2024, 2, 1),
(10, 28, 2, '35HIJ456', 42000, 170.00, 3, 'Available', 2022, 3, 3);
GO

-- 18. Reservations tablosu
INSERT INTO [dbo].[Reservations] ([CustomerID], [CarID], [StartDate], [EndDate], [TotalPrice], [Status], [BranchID]) VALUES 
(1, 4, '2024-12-01 10:00:00', '2024-12-05 10:00:00', 1680.00, 'Confirmed', 2),
(2, 1, '2024-12-10 09:00:00', '2024-12-15 09:00:00', 600.00, 'Pending', 1),
(3, 3, '2024-12-15 14:00:00', '2024-12-18 14:00:00', 1050.00, 'Confirmed', 1),
(4, 7, '2024-11-20 11:00:00', '2024-11-25 11:00:00', 800.00, 'Completed', 2),
(5, 2, '2024-11-10 13:00:00', '2024-11-12 13:00:00', 360.00, 'Completed', 2);
GO

-- 19. CustomerPayments tablosu
INSERT INTO [dbo].[CustomerPayments] ([ReservationID], [AccountID], [CustomerID], [Amount], [PaymentDate], [Description]) VALUES 
(1, 2, 1, 1680.00, '2024-12-01 09:30:00', 'Reservation payment for BMW X3'),
(4, 2, 4, 800.00, '2024-11-20 10:45:00', 'Reservation payment for Ford Focus'),
(5, 2, 5, 360.00, '2024-11-10 12:15:00', 'Reservation payment for Toyota Camry');
GO

-- 20. SalaryPayment tablosu
INSERT INTO [dbo].[SalaryPayment] ([AccountID], [EmployeeID], [Amount], [PaymentDate]) VALUES 
(1, 1, 8500.00, '2024-11-30 16:00:00'),
(2, 2, 9200.00, '2024-11-30 16:00:00'),
(3, 3, 8800.00, '2024-11-30 16:00:00');
GO

-- 21. Feedbacks tablosu
INSERT INTO [dbo].[Feedbacks] ([CustomerID], [ReservationID], [Rating], [Comment], [CreatedDate]) VALUES 
(4, 4, 5, 'Excellent service! The car was clean and the staff was very helpful.', '2024-11-26 15:30:00'),
(5, 5, 4, 'Good experience overall. The car was in good condition.', '2024-11-13 14:20:00');
GO

-- 22. CustomerDocuments tablosu
INSERT INTO [dbo].[CustomerDocuments] ([CustomerID], [DocumentType], [DocumentNumber], [IssueDate], [ExpiryDate], [DocumentStatus], [VerificationStatus], [VerificationDate], [VerifiedBy]) VALUES 
(1, 'Drivers License', 'DL123456789', '2020-05-15 00:00:00', '2030-05-15 00:00:00', 'Valid', 'Verified', '2024-11-01 10:00:00', 2),
(1, 'ID Card', 'ID12345678901', '2019-03-20 00:00:00', '2029-03-20 00:00:00', 'Valid', 'Verified', '2024-11-01 10:05:00', 2),
(2, 'Drivers License', 'DL234567890', '2021-08-10 00:00:00', '2031-08-10 00:00:00', 'Valid', 'Verified', '2024-11-02 11:00:00', 3),
(3, 'Drivers License', 'DL345678901', '2020-12-05 00:00:00', '2030-12-05 00:00:00', 'Valid', 'Verified', '2024-11-03 09:30:00', 2),
(4, 'Drivers License', 'DL456789012', '2022-01-18 00:00:00', '2032-01-18 00:00:00', 'Valid', 'Verified', '2024-11-04 14:15:00', 3),
(5, 'Drivers License', 'DL567890123', '2021-06-25 00:00:00', '2031-06-25 00:00:00', 'Valid', 'Verified', '2024-11-05 16:45:00', 2);
GO

-- 23. Insurance tablosu
INSERT INTO [dbo].[Insurance] ([CarID], [PolicyNumber], [InsuranceCompany], [StartDate], [EndDate], [CoverageType], [Premium], [Status]) VALUES 
(1, 'POL2024001', 'Axa Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Comprehensive', 2400.00, 'Active'),
(2, 'POL2024002', 'Allianz Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Comprehensive', 2800.00, 'Active'),
(3, 'POL2024003', 'Zurich Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Premium', 4200.00, 'Active'),
(4, 'POL2024004', 'Axa Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Premium', 5000.00, 'Active'),
(5, 'POL2024005', 'Allianz Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Premium', 4600.00, 'Active'),
(6, 'POL2024006', 'Zurich Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Comprehensive', 3200.00, 'Active'),
(7, 'POL2024007', 'Axa Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Basic', 1800.00, 'Active'),
(8, 'POL2024008', 'Allianz Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Basic', 1600.00, 'Active'),
(9, 'POL2024009', 'Zurich Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Basic', 1700.00, 'Active'),
(10, 'POL2024010', 'Axa Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Basic', 1500.00, 'Active'),
(11, 'POL2024011', 'Allianz Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Premium', 5400.00, 'Active'),
(12, 'POL2024012', 'Zurich Sigorta', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Comprehensive', 2600.00, 'Active');
GO

-- 24. Maintenance tablosu
INSERT INTO [dbo].[Maintenance] ([CarID], [StartDate], [EndDate], [Description], [Cost], [MaintenanceType], [ServiceProvider], [Status]) VALUES 
(9, '2024-11-15 08:00:00', '2024-11-17 17:00:00', 'Regular maintenance and oil change', 850.00, 'Routine', 'AutoService Plus', 'Completed'),
(3, '2024-10-20 09:00:00', '2024-10-22 16:00:00', 'Brake system check and tire replacement', 1200.00, 'Repair', 'Premium Auto Care', 'Completed'),
(7, '2024-12-05 10:00:00', NULL, 'Engine diagnostic and repair', 2500.00, 'Repair', 'Expert Motors', 'In Progress');
GO

-- 25. Promotion tablosu
INSERT INTO [dbo].[Promotion] ([Code], [Description], [DiscountType], [DiscountValue], [StartDate], [EndDate], [MinRentalDays], [MaxDiscount], [UsageLimit], [UsageCount], [Status]) VALUES 
('WELCOME20', 'Welcome discount for new customers', 'Percentage', 20.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 3, 500.00, 100, 15, 'Active'),
('WINTER25', 'Winter season special discount', 'Percentage', 25.00, '2024-12-01 00:00:00', '2025-02-28 23:59:59', 5, 750.00, 50, 8, 'Active'),
('WEEKEND50', 'Weekend special fixed discount', 'Fixed Amount', 50.00, '2024-11-01 00:00:00', '2024-12-31 23:59:59', 2, 50.00, 200, 42, 'Active'),
('LONGTERM', 'Long term rental discount', 'Percentage', 30.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 14, 1000.00, 30, 5, 'Active');
GO

-- 26. AuditLog tablosu
INSERT INTO [dbo].[AuditLog] ([UserID], [ActionType], [EntityType], [EntityID], [OldValue], [NewValue], [ActionDate], [IPAddress], [UserAgent]) VALUES 
(2, 'CREATE', 'Car', 1, NULL, 'New car added: Toyota Corolla 34ABC123', '2024-11-01 10:15:00', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'),
(3, 'UPDATE', 'Car', 4, 'Status: Available', 'Status: Rented', '2024-12-01 10:00:00', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'),
(2, 'CREATE', 'Reservation', 1, NULL, 'New reservation created for customer ID: 1', '2024-12-01 09:45:00', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'),
(4, 'UPDATE', 'Customer', 1, 'Phone: +905559876543', 'Phone: +905559876543, Documents verified', '2024-11-01 10:30:00', '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'),
(1, 'CREATE', 'Branch', 1, NULL, 'New branch created: Istanbul Kadikoy Branch', '2024-10-15 14:20:00', '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)');
GO

PRINT 'Sample data has been successfully inserted into all tables!';
PRINT 'Database is now ready for testing and development.';
PRINT '';
PRINT 'Sample data includes:';
PRINT '- 4 user roles (Admin, Manager, Employee, Customer)';
PRINT '- 11 users with different roles and SIMPLE PASSWORDS for testing';
PRINT '- 4 branches in different cities';
PRINT '- 12 cars from various brands';
PRINT '- 5 reservations (some completed, some active)';
PRINT '- Customer documents and verifications';
PRINT '- Insurance records for all cars';
PRINT '- Maintenance records';
PRINT '- Promotions and discounts';
PRINT '- Payment records';
PRINT '- Audit logs for tracking changes';
PRINT '';
PRINT 'TEST LOGIN CREDENTIALS:';
PRINT 'Admin: admin / admin123';
PRINT 'Managers: manager1 / manager123, manager2 / manager123';
PRINT 'Employees: employee1/2/3 / employee123';
PRINT 'Customers: customer1/2/3/4/5 / customer123';
GO 