# Claims Search UI - Angular 17+ Implementation

A responsive Angular 17+ application for searching claims, filtering by multiple criteria, populating grids, and exporting datasets.

## 🚀 Project Overview

This project implements a comprehensive Claims Search & Filter UI system with both frontend (Angular 17+) and backend (Spring Boot + Java 21) components.

### ✅ Completed Features

- **Modern Angular 17+ Implementation** with standalone components
- **Reactive Forms** with Angular FormBuilder and proper validation
- **Angular Signals** for state management and reactive updates
- **Material Design** components with custom styling
- **Responsive Design** that works from mobile to desktop
- **Server-side Search & Filtering** with rich filter options
- **Typeahead/Autocomplete** components for employers, programs, policies
- **Advanced Data Grid** with proper column structure
- **Sorting & Filtering** for individual columns
- **Pagination** with configurable page sizes
- **Export Functionality** (CSV, Excel, JSON)
- **URL-based State Management** for bookmarking and back button support
- **Loading States & Error Handling** with user-friendly feedback
- **Status Indicators** including red "R" badge for reopened claims

## 📁 Project Structure

```
minnal/
├── claims-ui/                    # Angular 17+ Frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── features/
│   │   │   │   └── claims/
│   │   │   │       ├── claims.component.ts
│   │   │   │       ├── claims.component.html
│   │   │   │       ├── claims.component.scss
│   │   │   │       └── column-filter/
│   │   │   │           └── column-filter.component.ts
│   │   │   ├── models/
│   │   │   │   └── claim.ts
│   │   │   ├── services/
│   │   │   │   ├── claims.service.ts
│   │   │   │   └── interceptors/
│   │   │   └── app.component.ts
│   │   └── styles.scss
│   ├── package.json
│   └── angular.json
├── thinklife/thinklife/          # Spring Boot Backend
│   ├── src/main/java/com/thinkingcode/thinklife/
│   │   ├── controller/
│   │   │   └── ClaimController.java
│   │   ├── repo/
│   │   │   └── ClaimSearchRepository.java
│   │   └── dto/
│   │       └── ClaimFilter.java
│   └── pom.xml
└── README.md                     # This file
```

## 🔧 Tech Stack

### Frontend
- **Angular 17+** with standalone components
- **Angular Material 17+** for UI components
- **TypeScript 5.9+** with strict mode
- **RxJS 7.8+** for reactive programming
- **SCSS** for styling with CSS variables
- **Angular Signals** for state management

### Backend
- **Java 21** with Spring Boot 3.2
- **Spring Web** for REST APIs
- **Spring Data JPA** for database operations
- **PostgreSQL** database
- **Apache POI** for Excel export
- **Maven** for dependency management

## 🎯 Key Features Implemented

### 1. Enhanced Search Form
- **Reactive Forms** with proper validation
- **Typeahead Components** for employers, programs, policies
- **Date Range Pickers** for incident dates
- **Multi-select Dropdowns** for status, insurance types, organizations
- **Form State Management** with URL synchronization

### 2. Advanced Data Grid
- **Column Structure**: Select checkbox, Claim #(link), Claimant ID, Suffix, Claimant Name, Incident Date, Status, Insured, Examiner, SSN
- **Status Indicators** with red "R" badge for reopened claims
- **Sortable Columns** with server-side sorting
- **Individual Column Filters** with text, select, and date filter types
- **Server-side Pagination** with configurable page sizes (10, 20, 50, 100)

### 3. Export Functionality
- **Multiple Formats**: CSV, Excel (XLSX), JSON
- **Server-side Generation** to handle large datasets
- **Filtered Exports** that respect current search criteria
- **Download Progress** indicators

### 4. State Management
- **Angular Signals** for reactive state updates
- **URL-based Filters** for bookmarking and sharing
- **Form State Persistence** across page refreshes
- **Error State Management** with user-friendly messages

### 5. Responsive Design
- **Mobile-first Approach** with CSS Grid and Flexbox
- **Breakpoint Management** (480px, 768px, 1024px, 1280px)
- **Touch-friendly Controls** for mobile devices
- **Horizontal Scrolling** for tables on smaller screens

## 🔌 API Endpoints

### Core Endpoints
```
GET /api/claims - Main claims search with all filters
GET /api/fetchMyqueue - Queue-style claims retrieval
GET /api/claims/export - Export claims (CSV/XLSX/JSON)
```

### Dropdown APIs
```
GET /api/lookups/insurance-types
GET /api/lookups/statuses
GET /api/lookups/claimant-types
GET /api/lookups/organizations
```

### Typeahead APIs
```
GET /api/typeahead/employers?q={query}
GET /api/typeahead/programs?q={query}
GET /api/typeahead/policies?q={query}
GET /api/typeahead/underwriters?q={query}
```

## 🏃‍♂️ Getting Started

### Prerequisites
- Node.js 18+ and npm
- Java 21+
- PostgreSQL database
- Maven 3.8+

### Frontend Setup
```bash
cd claims-ui
npm install
ng serve
```
The app will be available at `http://localhost:4200`

### Backend Setup
```bash
cd thinklife/thinklife
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`

## 🎨 UI/UX Features

### Search Form
- **Clean Layout** with organized form sections
- **Real-time Validation** with error messages
- **Auto-save** functionality with URL synchronization
- **Clear/Reset** functionality with confirmation

### Data Table
- **Fixed Header** that remains visible while scrolling
- **Row Selection** with bulk operations
- **Hover Effects** for better interaction feedback
- **Loading Skeletons** during data fetch
- **Empty States** with helpful guidance

### Column Filtering
- **Individual Column Filters** accessible via filter icons
- **Different Filter Types**:
  - Text filters for names, IDs, etc.
  - Select filters for status, categories
  - Date range filters for dates
- **Active Filter Indicators** showing applied filters
- **Quick Clear** functionality for each filter

### Responsive Features
- **Collapsible Navigation** on mobile
- **Stackable Form Fields** on smaller screens
- **Horizontal Table Scrolling** with sticky columns
- **Touch-optimized Controls** with appropriate sizing

## 🔍 Search & Filter Capabilities

### Basic Search
- **Quick Search** across multiple fields simultaneously
- **Wildcard Support** for partial matches
- **Case-insensitive** search

### Advanced Filters
- **Claim Number** with exact/contains/starts with options
- **Claimant Name** search with flexible matching
- **Date Ranges** for incident dates
- **Status Filtering** with multi-select
- **Insurance Type** filtering
- **Organization** filtering (1-4 levels)
- **Geographic Filtering** by loss state
- **Examiner Assignment** filtering

### Column-level Filtering
- **Text Filters**: Claim numbers, names, IDs
- **Select Filters**: Status, categories with multiple selection
- **Date Filters**: Incident dates with range selection
- **Real-time Application** with debounced input

## 📊 Performance Features

### Frontend Optimizations
- **OnPush Change Detection** for optimal performance
- **Lazy Loading** of components and modules
- **Virtual Scrolling** for large datasets
- **Debounced Search** to reduce API calls
- **Caching** of dropdown data

### Backend Optimizations
- **Indexed Database Queries** for fast search
- **Pagination** to limit result sets
- **Efficient SQL** with proper joins
- **Connection Pooling** for database access
- **Streaming Exports** for large files

## 🧪 Testing & Quality

### Code Quality
- **TypeScript Strict Mode** enabled
- **ESLint** with Angular recommended rules
- **Prettier** for consistent code formatting
- **Husky** pre-commit hooks

### Performance Metrics
- **Lighthouse Score** optimization
- **Bundle Size Analysis** with webpack-bundle-analyzer
- **Core Web Vitals** monitoring
- **API Response Time** monitoring

## 🔐 Security Features

### Frontend Security
- **XSS Protection** with Angular's built-in sanitization
- **CSRF Protection** with Angular's CSRF interceptor
- **Content Security Policy** headers
- **Secure HTTP-only Cookies** for authentication

### Backend Security
- **CORS Configuration** for cross-origin requests
- **Input Validation** with Bean Validation
- **SQL Injection Protection** with prepared statements
- **Authentication Interceptors** for secure API access

## 🚀 Deployment

### Frontend Deployment
```bash
ng build --configuration production
# Deploy dist/claims-ui to web server
```

### Backend Deployment
```bash
./mvnw clean package
java -jar target/thinklife-1.0-SNAPSHOT.jar
```

## 📈 Future Enhancements

### Planned Features
- **Advanced Analytics** dashboard
- **Bulk Operations** for claim management
- **Real-time Notifications** for claim updates
- **Audit Trail** functionality
- **Advanced Reporting** with charts and graphs
- **Mobile App** version with native features

### Technical Improvements
- **Unit Test Coverage** to 90%+
- **E2E Testing** with Cypress
- **Performance Monitoring** with APM tools
- **CI/CD Pipeline** automation
- **Docker Containerization** for easy deployment

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is proprietary and confidential. All rights reserved.

---

## 📞 Support

For questions or support, please contact the development team or create an issue in the project repository.

**Project Status**: ✅ Complete and Ready for Production