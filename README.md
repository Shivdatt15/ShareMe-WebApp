# 📁 ShareMe — Secure File Sharing Platform

ShareMe is a full-stack file sharing web application that allows authenticated users to upload, manage, share, and download files securely.

The application uses **Clerk for authentication**, **Spring Boot for the REST API**, **MongoDB Atlas for data persistence**, and **Cloudinary for cloud file storage**.

Users can upload multiple files, manage their uploaded files, make files public, generate shareable links, and allow anyone with a public link to view and download a file.

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Security Features](#-security-features)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [How It Works](#-how-it-works)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Database Design](#-database-design)
- [Deployment](#-deployment)
- [Key Concepts](#-key-concepts)


---

# ✨ Features

### 🔐 Authentication

- Clerk-based user authentication
- Secure JWT authentication between frontend and backend
- User identity is obtained from the verified Clerk JWT
- Stateless authentication using Spring Security
- Automatic user profile provisioning through Clerk webhooks

### 📤 File Upload

- Upload multiple files
- Multipart file upload support
- File size validation
- Cloud-based file storage using Cloudinary
- File metadata stored separately in MongoDB Atlas
- Upload credit system

### 📂 File Management

- View recently uploaded files
- View all personal files
- Delete uploaded files
- Download files
- Toggle files between public and private
- File ownership validation

### 🔗 Public File Sharing

- Generate shareable public URLs
- Anyone with a public link can access the file
- Public file preview page
- Public file download
- No Clerk authentication required for public file access

Example:

```text
https://your-frontend-domain.com/file/{fileId}
```

### 💳 Upload Credit System

- Users receive initial upload credits=10
- Credits are consumed when files are uploaded
- Backend validates available credits before uploading
- Credits are stored in MongoDB

### 🎨 Modern User Interface

- React-based frontend
- Responsive dashboard
- File cards
- Upload interface
- File sharing modal
- Confirmation dialogs
- Toast notifications
- Loading and error states


# 🏗️ Architecture

ShareMe follows a **client-server architecture** with separate frontend and backend applications.

```text
                         ┌──────────────────┐
                         │      Clerk       │
                         │  Authentication  │
                         └────────┬─────────┘
                                  │
                                  │ JWT
                                  ▼
┌──────────────────┐      ┌──────────────────┐
│                  │      │                  │
│   React/Vite     │─────▶│   Spring Boot    │
│    Frontend      │ HTTP │     Backend      │
│                  │      │                  │
└──────────────────┘      └───────┬──────────┘
                                  │
                                  │
                                  ├──────────────┐
                                  │              │
                                  ▼              ▼
                          MongoDB Atlas    Cloudinary
                             Metadata         File Storage
```

---

## 🔄 Main Components

### 1. Frontend

The frontend is responsible for:

- User interface
- Clerk authentication
- JWT token retrieval
- API communication
- File selection
- Upload progress/UI state
- File management
- Public file viewing
- Shareable links

The frontend sends the Clerk JWT with authenticated API requests:

```text
Authorization: Bearer <Clerk-JWT>
```

---

### 2. Backend

The Spring Boot backend provides:

- REST APIs
- JWT validation
- User profile management
- File metadata management
- Upload credit management
- Cloudinary integration
- MongoDB integration
- Public file access
- File ownership validation

---

### 3. MongoDB Atlas

MongoDB stores application metadata rather than the actual file contents.

Main collections:

```text
profiles
user_credits
file_metadata
```

---

### 4. Cloudinary

Cloudinary stores the actual uploaded files.

MongoDB stores information such as:

```text
file ID
file name
file size
Clerk user ID
Cloudinary public ID
Cloudinary URL
public/private status
upload timestamp
```

This separates **file storage** from **application metadata**.

---

# 🔐 Security Architecture

ShareMe uses multiple layers of security.

```text
User
  │
  ▼
Clerk Authentication
  │
  ▼
Clerk JWT
  │
  ▼
Frontend
  │
  │ Authorization: Bearer <JWT>
  ▼
ClerkJwtAuthFilter
  │
  ├── Validate JWT
  ├── Validate signature
  ├── Validate issuer
  ├── Read Clerk user ID
  │
  ▼
SecurityContext
  │
  ▼
Spring Security
  │
  ▼
Controller
  │
  ▼
Service
  │
  ├── Ownership validation
  ├── Credit validation
  └── Database operations
```

---

# 🛡️ Security Features

## 1. Clerk JWT Authentication

Authenticated requests contain a Clerk JWT:

```http
Authorization: Bearer <JWT>
```

The backend's `ClerkJwtAuthFilter`:

1. Reads the Authorization header
2. Extracts the Bearer token
3. Reads the JWT `kid`
4. Retrieves the corresponding public key
5. Validates the RSA signature
6. Validates the issuer
7. Extracts the Clerk user ID from the JWT subject
8. Creates the Spring Security authentication
9. Stores authentication in `SecurityContextHolder`

The authenticated Clerk ID can then be used by the application services.

---

## 2. JWKS-Based JWT Verification

The backend retrieves Clerk public keys using the configured JWKS endpoint.

```text
Clerk JWT
    ↓
kid
    ↓
JWKS Provider
    ↓
Public RSA Key
    ↓
Signature Verification
    ↓
Authenticated User
```

The public keys are cached by `ClerkJwksProvider` to avoid requesting the keys for every API request.

---

## 3. Stateless Authentication

Spring Security uses:

```java
SessionCreationPolicy.STATELESS
```

The backend does not maintain traditional server-side login sessions.

Each authenticated API request independently provides its JWT.

---

## 4. File Ownership Validation

Users can only modify their own files.

Operations such as:

```text
Delete file
Toggle public/private
Upload file
View personal files
```

use the authenticated Clerk ID to identify the current user.

---

## 5. Public/Private File Access

Files have a public/private state.

Private files are accessible only to the owner through authenticated APIs.

Public files can be accessed through:

```text
GET /files/public/{id}
```

and downloaded through:

```text
GET /files/download/{id}
```

---

## 6. CORS Protection

The backend restricts frontend access using the configured frontend URL.

Allowed requests include:

```text
GET
POST
PUT
PATCH
DELETE
OPTIONS
```

The production frontend domain is configured through:

```text
FRONTEND_URL
```

---

# 🛠️ Technology Stack

## Frontend

- **React** — User interface
- **React Router** — Client-side routing
- **Tailwind CSS** — Styling
- **Axios** — HTTP client
- **Clerk React** — Authentication
- **Lucide React** — Icons
- **React Hot Toast** — Notifications

---

## Backend

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web MVC**
- **Spring Security**
- **Spring Data MongoDB**
- **JJWT 0.13.0**
- **Cloudinary SDK**
- **Maven**

---

## Database & Storage

- **MongoDB Atlas** — Application metadata
- **Cloudinary** — File storage

---

## Authentication

- **Clerk**
- **JWT**
- **JWKS / RSA public-key verification**
- **Spring Security**

---

# 🚀 Getting Started

## Prerequisites

Make sure you have:

- Java 21
- Node.js
- npm
- Maven
- MongoDB Atlas account
- Cloudinary account
- Clerk account

---

# 📦 Installation

## 1. Clone the Repository

```bash
git clone https://github.com/yourusername/shareme-webapp.git
cd shareme-webapp
```

---

# 🖥️ Frontend Setup

Navigate to the frontend:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Create a `.env` file:

```env
VITE_CLERK_PUBLISHABLE_KEY=your_clerk_publishable_key
VITE_API_BASE_URL=http://localhost:8080/api/v1.0
```

Start the development server:

```bash
npm run dev
```

The frontend will normally run on:

```text
http://localhost:5173
```

---

# ⚙️ Backend Setup

Navigate to the backend project.

Create the required environment variables.

Example:

```env
MONGODB_URI=your_mongodb_atlas_connection_string

FRONTEND_URL=http://localhost:5173

CLERK_ISSUER=https://your-clerk-domain.clerk.accounts.dev
CLERK_JWKS_URL=https://your-clerk-domain.clerk.accounts.dev/.well-known/jwks.json
CLERK_WEBHOOK_SECRET=your_clerk_webhook_secret

CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

Run the backend using Maven:

```bash
mvn spring-boot:run
```

The backend will normally run on:

```text
http://localhost:8080
```

Because the application uses:

```properties
server.servlet.context-path=/api/v1.0
```

the API base URL becomes:

```text
http://localhost:8080/api/v1.0
```

---

# 🔑 Environment Variables

| Variable | Purpose |
|---|---|
| `MONGODB_URI` | MongoDB Atlas connection string |
| `FRONTEND_URL` | Allowed frontend origin |
| `CLERK_ISSUER` | Clerk JWT issuer |
| `CLERK_JWKS_URL` | Clerk public key endpoint |
| `CLERK_WEBHOOK_SECRET` | Clerk webhook verification secret |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `VITE_CLERK_PUBLISHABLE_KEY` | Clerk frontend publishable key |
| `VITE_API_BASE_URL` | Backend API base URL |


---

# 💡 How It Works

## 👤 User Authentication Flow

```text
User
 ↓
Clerk Sign In / Sign Up
 ↓
Clerk authenticates user
 ↓
Frontend obtains JWT
 ↓
Axios sends JWT
 ↓
Spring Boot
 ↓
ClerkJwtAuthFilter
 ↓
JWT verification
 ↓
SecurityContext
 ↓
Authenticated API request
```

---

# 👤 User Provisioning Flow

When a new user is created in Clerk:

```text
New Clerk User
      ↓
Clerk user.created webhook
      ↓
Spring Boot
      ↓
ClerkWebhookController
      ↓
ProfileService
      ↓
MongoDB
      │
      ├── profiles
      │
      └── user_credits
```

The profile contains information such as:

```text
clerkId
email
firstName
lastName
photoUrl
credits
createdAt
```

The initial user credit record is created with the configured initial credit amount.

---

# 📤 File Upload Flow

```text
User selects files
       ↓
React Upload UI
       ↓
POST /files/upload
       ↓
Clerk JWT
       ↓
ClerkJwtAuthFilter
       ↓
Authenticated Clerk ID
       ↓
Profile lookup
       ↓
Credit validation
       ↓
Cloudinary upload
       ↓
File metadata saved in MongoDB
       ↓
Credit consumed
       ↓
Response returned to frontend
```

The actual file is stored in Cloudinary while metadata is stored in MongoDB.

---

# 📂 Fetch User Files

```text
Dashboard
    ↓
GET /files/my
    ↓
JWT Authentication
    ↓
Get current Clerk ID
    ↓
Find files by clerkId
    ↓
MongoDB
    ↓
Return file metadata
```

---

# 🔗 Public File Sharing Flow

When the owner makes a file public:

```text
Private File
     ↓
Toggle Public
     ↓
PATCH /files/{id}/toggle-public
     ↓
isPublic = true
```

The frontend creates a link such as:

```text
https://your-frontend-domain.com/file/{fileId}
```

When another user opens that link:

```text
Public URL
    ↓
Netlify
    ↓
React Router
    ↓
/file/:fileId
    ↓
PublicFileView
    ↓
GET /files/public/{fileId}
    ↓
Spring Boot
    ↓
MongoDB
    ↓
Verify isPublic
    ↓
Return file metadata
```

No Clerk login is required for public file viewing.

---

# ⬇️ File Download Flow

```text
User clicks Download
       ↓
GET /files/download/{fileId}
       ↓
Backend finds file metadata
       ↓
Checks public status
       ↓
Gets Cloudinary URL
       ↓
Redirects/downloads file
```

---

# 🗄️ Database Design

## `profiles`

Stores application-level user information.

```text
ProfileDocument
├── id
├── clerkId
├── email
├── firstName
├── lastName
├── credits
├── photoUrl
└── createdAt
```

---

## `user_credits`

Stores upload credit information.

```text
UserCredits
├── id
├── clerkId
└── credits
```

`clerkId` uniquely identifies the Clerk user.

---

## `file_metadata`

Stores information about uploaded files.

Conceptually:

```text
FileMetadataDocument
├── id
├── clerkId
├── name
├── size
├── publicId
├── url
├── isPublic
└── uploadedAt
```

The exact fields should match the current `FileMetadataDocument` implementation.

---

# 📡 API Documentation

Base URL:

```text
/api/v1.0
```

---

## Authentication

Authenticated endpoints require:

```http
Authorization: Bearer <Clerk-JWT>
```

---

## Files

### Upload Files

```http
POST /files/upload
```

Uploads one or more files.

Request:

```text
multipart/form-data
```

The backend validates:

- Authentication
- File count
- Available credits
- File size
- Cloudinary upload
- Metadata persistence

---

### Get My Files

```http
GET /files/my
```

Returns files belonging to the authenticated user.

---

### Get Public File

```http
GET /files/public/{id}
```

Returns metadata for a public file.

Authentication is not required.

---

### Download Public File

```http
GET /files/download/{id}
```

Downloads/redirects to the publicly shared file.

Authentication is not required for public files.

---

### Delete File

```http
DELETE /files/{id}
```

Deletes a file owned by the authenticated user.

---

### Toggle Public Status

```http
PATCH /files/{id}/toggle-public
```

Changes a file between:

```text
public
private
```

---

# 👤 User APIs

### Get User Credits

```http
GET /users/credits
```

Returns the authenticated user's available upload credits.

---

### Register/Profile

```http
POST /users/register
```

Creates or updates an application profile where applicable.

---

# 🔔 Clerk Webhook

### Clerk Webhook Endpoint

```http
POST /webhooks/clerk
```

Because the application context path is:

```text
/api/v1.0
```

the production URL is:

```text
https://your-railway-domain/api/v1.0/webhooks/clerk
```

Supported Clerk events include:

```text
user.created
user.updated
user.deleted
```

The webhook keeps the application's MongoDB user records synchronized with Clerk users.

---

# 📁 Project Structure

The project is organized into separate frontend and backend applications.

├── ShareMe-WebApp/
       ├── Backend
       ├── Frontend
             

## Backend

```text
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/shareme_webapp/
│       │       ├── config/
│       │       │   └── CloudinaryConfig.java
│       │       │
│       │       ├── controller/
│       │       │   ├── FileController.java
│       │       │   ├── ProfileController.java
│       │       │   └── ClerkWebhookController.java
│       │       │
│       │       ├── document/
│       │       │   ├── ProfileDocument.java
│       │       │   ├── UserCredits.java
│       │       │   └── FileMetadataDocument.java
│       │       │
│       │       ├── repository/
│       │       │   ├── ProfileRepository.java
│       │       │   ├── UserCreditsRepository.java
│       │       │   └── FileMetadataRepository.java
│       │       │
│       │       ├── security/
│       │       │   ├── ClerkJwtAuthFilter.java
│       │       │   └── ClerkJwksProvider.java
│       │       │
│       │       └── service/
│       │           ├── ProfileService.java
│       │           ├── UserCreditsService.java
│       │           └── FileMetadataService.java
│       │
│       └── resources/
│           └── application.properties
│
└── pom.xml
```

## Frontend

```text
frontend/
├── public/
│   ├── _redirects
│   └── sharemelogo.jpg
│
├── src/
│   ├── components/
│   │   ├── Navbar
│   │   ├── FileCard
│   │   ├── LinkShareModal
│   │   ├── ConfirmationDialog
│   │   └── ...
│   │
│   ├── pages/
│   │   ├── Dashboard
│   │   ├── PublicFileView
│   │   ├── MyFiles
│   │   └── ...
│   │
│   ├── context/
│   │   └── UserCreditsContext
│   │
│   ├── util/
│   │   └── apiEndpoints.js
│   │
│   └── App.jsx
│
├── package.json
└── vite.config.js
```

---

# ☁️ Deployment Architecture

ShareMe is deployed using separate cloud services.

```text
                     Internet
                        │
             ┌──────────┴──────────┐
             │                     │
             ▼                     ▼
        ┌─────────┐           ┌─────────┐
        │ Netlify │           │  Clerk  │
        │Frontend │           │  Auth   │
        └────┬────┘           └────┬────┘
             │                     │
             │ HTTPS + JWT         │ JWT
             │                     │ Webhook
             ▼                     ▼
        ┌─────────────────────────────┐
        │          Railway            │
        │       Spring Boot API       │
        └─────────────┬───────────────┘
                      │
             ┌────────┴────────┐
             │                 │
             ▼                 ▼
      ┌─────────────┐    ┌────────────┐
      │ MongoDB     │    │ Cloudinary │
      │ Atlas       │    │   Storage  │
      └─────────────┘    └────────────┘
```

---

# 🌐 Production Configuration

Frontend:

```text
Netlify
```

Backend:

```text
Railway
```

Database:

```text
MongoDB Atlas
```

File storage:

```text
Cloudinary
```

Authentication:

```text
Clerk
```

The frontend API environment variable should point to the Railway backend:

```env
VITE_API_BASE_URL=https://your-railway-domain/api/v1.0
```

---

# 🔄 Complete Application Flow

```text
                    ┌─────────────┐
                    │    User     │
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
                    │   Clerk     │
                    │    Auth     │
                    └──────┬──────┘
                           │
                         JWT
                           │
                           ▼
                    ┌─────────────┐
                    │   React     │
                    │  Frontend   │
                    └──────┬──────┘
                           │
                    HTTPS + JWT
                           │
                           ▼
                 ┌──────────────────┐
                 │  Spring Security │
                 │                  │
                 │ ClerkJwtAuthFilter│
                 └────────┬─────────┘
                          │
                          ▼
                    ┌─────────────┐
                    │ Controllers │
                    └──────┬──────┘
                           │
                           ▼
                     ┌──────────┐
                     │ Services │
                     └────┬─────┘
                          │
               ┌──────────┴──────────┐
               │                     │
               ▼                     ▼
        ┌─────────────┐       ┌────────────┐
        │ MongoDB     │       │ Cloudinary │
        │ Atlas       │       │   Files    │
        └─────────────┘       └────────────┘
```

---


## ⭐ ShareMe

A secure and modern file-sharing platform built with a React + Spring Boot architecture.

```text
Upload → Store → Manage → Share → Download
```
