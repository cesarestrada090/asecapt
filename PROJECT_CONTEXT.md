# FITECH PROJECT CONTEXT

## 📋 PROJECT OVERVIEW
FITECH is a fitness platform that connects trainers with clients, offering personalized training services and premium memberships.

## 🏗️ DASHBOARD ARCHITECTURE

### Trainer Dashboard
- **Route**: `dashboard/trainer`
- **Purpose**: Interface for trainers to manage their services, clients, and contracts
- **Features**: Service management, client tracking, resource creation (diets/routines)

### Client Dashboard  
- **Route**: `dashboard/client`
- **Purpose**: Interface for clients to access their contracted services and premium features
- **Features**: Service access, premium content, profile management

## 💎 PREMIUM MEMBERSHIP SYSTEM

Clients can obtain premium access through two methods:

### 1. Contract-Based Premium
- **Trigger**: Automatically activated when generating a contract with a trainer
- **Duration**: Active for the duration of the service contract
- **Benefits**: Access to trainer's exclusive content and personalized services

### 2. Monthly Subscription Premium
- **Trigger**: Monthly payment subscription
- **Duration**: Renewable monthly basis
- **Benefits**: Platform-wide premium features and content

## 🔒 PRIVACY & COMMUNICATION POLICY

### Strict Privacy Rules
- **❌ NEVER display phone numbers** in any implementation
- **❌ NEVER display email addresses** in any implementation
- **✅ ALL communication must occur through the app** messaging system
- **🛡️ Protect personal contact information** at all interface levels

### Data Display Guidelines
```typescript
// ❌ WRONG - Never show personal contact info
<span>Email: {{ user.email }}</span>
<span>Phone: {{ user.phoneNumber }}</span>

// ✅ CORRECT - Show only necessary profile info
<span>Name: {{ user.firstName }} {{ user.lastName }}</span>
<span>Username: {{ user.username }}</span>
```

## 📝 CODE STANDARDS

### Comments & Logging Policy
- **✅ ALL code comments MUST be in ENGLISH**
- **✅ ALL log messages MUST be in ENGLISH**
- **🔄 Spanish comments/logs MUST be translated to English**

### Exception Handling
- **✅ Exception messages for end users MUST remain in SPANISH**
- **✅ Internal logs about exceptions MUST be in ENGLISH**

### Code Example Template
```java
// Load client profile data from database
log.info("Loading client profile for ID: {}", clientId);

try {
    // Process service contract creation
    return processContract(data);
} catch (Exception e) {
    // Log in English, exception message in Spanish for user
    log.error("Failed to create service contract for client: {}", clientId);
    throw new RuntimeException("No se pudo crear el contrato de servicio");
}
```

```typescript
// Initialize component data on load
ngOnInit() {
    console.log('Loading client dashboard data');
    this.loadClientData();
}

private handleError(error: any) {
    // Log in English
    console.error('Error loading client data:', error);
    
    // User message in Spanish
    this.showErrorMessage('No se pudieron cargar los datos del cliente');
}
```

## 🗄️ DATABASE ENTITIES

### Core Entities
- **`User`**: System users (clients and trainers)
- **`Person`**: Personal information linked to users
- **`ServiceContract`**: Contracts between clients and trainers
- **`TrainerService`**: Services offered by trainers
- **`ServiceType`**: Categories of available services
- **`membership_payments`**: Payment records for all transactions

### Key Relationships
```
User (1) ←→ (1) Person
User (1) ←→ (N) ServiceContract
TrainerService (1) ←→ (N) ServiceContract
ServiceType (1) ←→ (N) TrainerService
User (1) ←→ (N) membership_payments
```

## 💰 PAYMENT SYSTEM

### Payment Storage
- **Table**: `membership_payments`
- **Purpose**: Store all payment transactions (contracts and monthly subscriptions)
- **Records**: Both trainer service contracts and premium subscriptions

### Platform Commission
- **Commission Rate**: 5% (configurable)
- **Applied To**: All contracts between clients and trainers
- **Calculation**: 
  ```
  Platform Commission = Contract Total Amount × 5%
  Trainer Earnings = Contract Total Amount - Platform Commission
  ```

### Payment Types
1. **Service Contract Payments**: Client pays for trainer services
2. **Monthly Premium Subscriptions**: Client pays for platform premium features

### Financial Flow
```
Client Payment → Platform (5% commission) + Trainer (95% earnings)
```

## 🌐 API STRUCTURE

### Endpoint Pattern
```
Base URL: /v1/app/[resource]
```

### Examples
```
GET  /v1/app/clients/{id}/profile
GET  /v1/app/trainers/{id}/clients
POST /v1/app/service-contracts
GET  /v1/app/file-upload/view/{fileId}
```

### Response Format
```json
{
    "success": true,
    "data": { ... },
    "message": "Operation completed successfully"
}
```

## 🎯 USER TYPES

### Client (type = 1)
- Can contract trainer services
- Access to premium features through contracts or subscriptions
- Profile includes fitness goals and personal information

### Trainer (type = 2)  
- Can offer services to clients
- Manage client relationships and create resources
- Access to trainer-specific dashboard features

## 📱 FRONTEND ARCHITECTURE

### Component Structure
```
src/app/components/
├── dashboard/
│   ├── clients/           # Client management (trainers view)
│   ├── client-profile/    # Individual client profiles
│   ├── services/          # Service management
│   └── ...
├── profile/               # User profile management
└── ...
```

### Service Layer
```
src/app/services/
├── auth.service.ts        # Authentication
├── profile.service.ts     # User profiles
├── trainer-client.service.ts  # Trainer-client relationships
└── ...
```

## 🔧 DEVELOPMENT GUIDELINES

### File Naming
- Use kebab-case for files: `client-profile.component.ts`
- Use PascalCase for classes: `ClientProfileComponent`
- Use camelCase for variables: `clientProfile`

### Error Handling
```typescript
// Template for error handling
try {
    // Operation logic here
} catch (error) {
    console.error('Operation failed:', error); // English log
    this.showError('La operación falló'); // Spanish user message
}
```

### Security Considerations
- Always validate user permissions before data access
- Never expose sensitive data in API responses
- Use proper authentication checks for all endpoints

---

**Last Updated**: December 2024  
**Version**: 1.0  
**Maintainer**: Development Team

> 📌 **Important**: This context should be referenced for all development decisions and implementations in the FITECH project. 