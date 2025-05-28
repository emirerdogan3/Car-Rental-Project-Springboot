# Car Rental System - Teknik Dokümantasyon

Bu dokümantasyon, Java Spring Boot tabanlı araç kiralama sisteminde uygulanan **SOLID prensipleri** ve **Design Pattern'ları** gerçek kod örnekleriyle açıklamaktadır.

## SOLID Prensipleri

### 1. Single Responsibility Principle (SRP)
Her sınıf sadece bir sorumluluğa sahiptir ve değişmek için sadece bir nedeni vardır.

**Örnekler:**
- **`UserService`**: Sadece kullanıcı işlemlerini yönetir
- **`CarService`**: Sadece araç işlemlerini yönetir  
- **`ReservationService`**: Sadece rezervasyon işlemlerini yönetir
- **`FeedbackService`**: Sadece feedback işlemlerini yönetir

```java
// src/main/java/com/rentacar/service/UserService.java
public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Integer id);
    UserDTO createUserByAdmin(UserDTO userDTO);
    void deleteUser(Integer id);
    // Sadece kullanıcı yönetimi ile ilgili metotlar
}
```

### 2. Open/Closed Principle (OCP)
Sınıflar genişletmeye açık, değişikliğe kapalıdır. Yeni özellikler mevcut kodu değiştirmeden eklenebilir.

**Örnek: Strategy Pattern ile Ödeme Yöntemleri**
```java
// src/main/java/com/rentacar/strategy/PaymentStrategy.java
public interface PaymentStrategy {
    void pay(double amount);
}

// Yeni ödeme yöntemi eklemek için mevcut kodu değiştirmiyoruz
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println(amount + " TL kredi kartı ile ödendi.");
    }
}

// Yeni bir ödeme yöntemi kolayca eklenebilir
public class DigitalWalletPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println(amount + " TL dijital cüzdan ile ödendi.");
    }
}
```

### 3. Liskov Substitution Principle (LSP)
Alt sınıflar, üst sınıflarının yerine geçebilir ve aynı davranışı sergilemelidir.

**Örnek: Service Interface ve Implementation'ları**
```java
// Tüm service implementasyonları interface'lerinin yerine geçebilir
CarService carService = new CarServiceImpl(); // Her zaman çalışır
UserService userService = new UserServiceImpl(); // Her zaman çalışır

// Controller'da interface kullanıyoruz, implementation değil
@RestController
public class CarController {
    private final CarService carService; // Interface, implementation değil
    
    public CarController(CarService carService) {
        this.carService = carService; // Herhangi bir implementation kabul eder
    }
}
```

### 4. Interface Segregation Principle (ISP)
Büyük interface'ler yerine, özelleşmiş küçük interface'ler kullanılır.

**Örnekler:**
```java
// Farklı sorumluluklar için ayrı interface'ler
public interface PaymentStrategy {
    void pay(double amount); // Sadece ödeme
}

public interface NotificationObserver {
    void update(String message); // Sadece bildirim
}

public interface NotificationSubject {
    void addObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);  
    void notifyObservers(String message); // Sadece observer yönetimi
}
```

### 5. Dependency Inversion Principle (DIP)
Üst seviye modüller alt seviye modüllere değil, soyutlamalara bağımlıdır.

**Örnek: Service Katmanında Dependency Injection**
```java
// src/main/java/com/rentacar/service/impl/ReservationServiceImpl.java
@Service
@RequiredArgsConstructor // Lombok ile constructor injection
public class ReservationServiceImpl implements ReservationService {
    // Interface'lere bağımlılık, concrete class'lara değil
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BranchService branchService; // Interface
    private final CustomerPaymentService customerPaymentService; // Interface
    private final MoneyAccountService moneyAccountService; // Interface
    
    // Implementation detayları Spring tarafından injection edilir
}
```

## Kullanılan Design Pattern'lar

### 1. Factory Pattern
Nesne oluşturma mantığını soyutlar ve uygun nesne tiplerini döndürür.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/factory/UserFactory.java
public class UserFactory {
    public static User createUser(String username, String passwordHash, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        return user;
    }

    public static Manager createManager(User user) {
        Manager manager = new Manager();
        manager.setUser(user);
        return manager;
    }

    public static Employee createEmployee(User user) {
        Employee employee = new Employee();
        employee.setUser(user);
        return employee;
    }
}

// UserServiceImpl'de kullanımı
private void createManagerProfile(User user) {
    Manager manager = UserFactory.createManager(user);
    manager.setFirstName("");
    manager.setLastName("");
    managerRepository.save(manager);
}
```

### 2. Singleton Pattern  
Bir sınıfın sadece bir instance'ının oluşturulmasını garanti eder.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/util/LoggerSingleton.java
public class LoggerSingleton {
    private static volatile LoggerSingleton instance;

    private LoggerSingleton() {
        // private constructor
    }

    public static LoggerSingleton getInstance() {
        if (instance == null) {
            synchronized (LoggerSingleton.class) {
                if (instance == null) {
                    instance = new LoggerSingleton();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

// Kullanımı
LoggerSingleton logger = LoggerSingleton.getInstance();
logger.log("Reservation created successfully");
```

### 3. Strategy Pattern
Algoritmaları encapsulate eder ve runtime'da değiştirilebilir hale getirir.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/strategy/PaymentStrategy.java
public interface PaymentStrategy {
    void pay(double amount);
}

// src/main/java/com/rentacar/strategy/CreditCardPayment.java
public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardHolder;
    private String expiry;
    private String cvv;

    public CreditCardPayment(String cardNumber, String cardHolder, String expiry, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiry = expiry;
        this.cvv = cvv;
    }

    @Override
    public void pay(double amount) {
        System.out.println(amount + " TL kredi kartı ile ödendi. [Kart: " + cardNumber + "]");
    }
}

// src/main/java/com/rentacar/strategy/PaymentContext.java
public class PaymentContext {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void pay(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Ödeme yöntemi seçilmedi!");
        }
        paymentStrategy.pay(amount);
    }
}

// Kullanımı
PaymentContext context = new PaymentContext();
context.setPaymentStrategy(new CreditCardPayment("1234567890123456", "John Doe", "12/25", "123"));
context.pay(250.0);
```

### 4. Observer Pattern
Bir nesnenin durumu değiştiğinde, bağımlı nesnelere otomatik bildirim gönderir.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/observer/NotificationObserver.java
public interface NotificationObserver {
    void update(String message);
}

// src/main/java/com/rentacar/observer/NotificationSubject.java
public interface NotificationSubject {
    void addObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(String message);
}

// src/main/java/com/rentacar/observer/EmailNotificationObserver.java
public class EmailNotificationObserver implements NotificationObserver {
    private final String email;

    public EmailNotificationObserver(String email) {
        this.email = email;
    }

    @Override
    public void update(String message) {
        System.out.println("E-posta gönderildi: " + email + " | Mesaj: " + message);
    }
}

// src/main/java/com/rentacar/observer/FeedbackNotificationService.java
public class FeedbackNotificationService implements NotificationSubject {
    private final List<NotificationObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }

    public void newFeedback(String feedbackMessage) {
        notifyObservers("Yeni feedback: " + feedbackMessage);
    }
}

// Kullanımı
FeedbackNotificationService feedbackService = new FeedbackNotificationService();
feedbackService.addObserver(new EmailNotificationObserver("manager@example.com"));
feedbackService.newFeedback("5 yıldız - Mükemmel hizmet!");
```

### 5. Facade Pattern
Karmaşık subsystem'leri basit bir interface arkasında gizler.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/facade/RentalFacade.java
@Component
public class RentalFacade {
    private final CarService carService;
    private final ReservationService reservationService;
    private final CustomerPaymentService paymentService;
    private final CarMapper carMapper;

    @Autowired
    public RentalFacade(CarService carService, 
                       ReservationService reservationService, 
                       CustomerPaymentService paymentService, 
                       CarMapper carMapper) {
        this.carService = carService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.carMapper = carMapper;
    }

    // Tek metotla kompleks işlemleri yönetir
    public Reservation rentAndPay(Car car, Reservation reservation, CustomerPayment payment) {
        // Birden fazla servisin koordinasyonu
        // 1. Araç durumu güncelle
        // 2. Rezervasyon oluştur  
        // 3. Ödeme işle
        // 4. Bildirim gönder
        throw new UnsupportedOperationException(
            "RentalFacade.rentAndPay is outdated. Use ReservationService.createReservationAndProcessPayment."
        );
    }
}
```

## Repository Pattern
Spring Data JPA ile Repository Pattern uygulanmıştır.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/repository/UserRepository.java
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    long countByRole_RoleName(String roleName);
    List<User> findByRole_RoleName(String roleName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

// Service'te kullanımı
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository; // Repository abstraction
    
    @Override
    public Optional<UserDTO> findDtoByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }
}
```

## Mapper Pattern (Data Transfer Object Pattern)
Entity'ler ve DTO'lar arasında dönüşüm için MapStruct kullanılmıştır.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/mapper/UserMapper.java
@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(source = "enabled", target = "enabled")
    UserDTO toDto(User user);

    @Mapping(source = "password", target = "passwordHash")
    @Mapping(source = "enabled", target = "enabled")
    User toEntity(UserDTO userDTO);
}
```

## Specification Pattern
Dinamik sorgu oluşturma için JPA Specification kullanılmıştır.

**Kod Örneği:**
```java
// src/main/java/com/rentacar/repository/specification/CarSpecification.java
@Component
public class CarSpecification {
    public Specification<Car> findByCriteria(CarFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filter.getBranchId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("branch").get("branchID"), filter.getBranchId()));
            }
            
            if (filter.getBrandId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("brandID"), filter.getBrandId()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

## Özet

Bu araç kiralama sistemi, modern Java development best practice'lerini uygulayarak:

- **Maintainable Code**: SOLID prensipleri ile sürdürülebilir kod
- **Flexible Architecture**: Design Pattern'lar ile esnek mimari  
- **Separation of Concerns**: Her katmanın kendi sorumluluğu
- **Dependency Injection**: Loose coupling ile test edilebilir kod
- **Clean Code**: Interface'ler ve abstraction'lar ile temiz kod

Bu yaklaşım, sistemin genişletilebilirliğini artırır ve gelecekteki değişikliklere adapte olmasını kolaylaştırır. 