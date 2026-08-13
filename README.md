# offerbase
# OfferBase

OfferBase is an intelligent job application tracker that connects to Gmail, identifies recruiting emails, and converts them into structured application updates.

Instead of manually maintaining a spreadsheet, users can track applications in list and Kanban views while OfferBase detects application confirmations, interviews, offers, and rejections from incoming email.

## Features

- Secure JWT-based authentication
- Job application CRUD with PostgreSQL persistence
- List and Kanban-style application views
- Gmail integration through OAuth 2.0
- Historical Gmail import and incremental synchronization
- Duplicate email prevention
- Recruiting-email relevance filtering
- Hybrid rule-based and machine-learning email classification
- Automatic company and position extraction
- Matching recruiting emails to existing applications
- User-approved application creation and status updates
- Human-in-the-loop ML feedback collection
- Persistent application event timeline

## Machine Learning

OfferBase uses a Python/scikit-learn text-classification pipeline to classify recruiting emails into five categories:

- APPLIED
- INTERVIEW
- OFFER
- REJECTED
- OTHER

Email subject, sender, and body text are transformed using TF-IDF features and classified using logistic regression.

The trained model is served through a FastAPI inference service and consumed by the Spring Boot backend.

OfferBase combines the ML classifier with a deterministic rule-based classifier. High-confidence agreement can be accepted, while ambiguous or conflicting predictions can be routed for review.

During development, the classifier achieved 97% accuracy on a separate 30-email holdout set.

## Architecture

Frontend:
- React
- TypeScript
- Tailwind CSS

Backend:
- Java
- Spring Boot
- Spring Security
- JPA / Hibernate

Database:
- PostgreSQL

ML Service:
- Python
- scikit-learn
- FastAPI

Integrations:
- Gmail API
- Google OAuth 2.0

## Email Processing Pipeline

Gmail  
→ Recruiting relevance filter  
→ Rule-based classifier  
→ ML classifier  
→ Hybrid decision layer  
→ Company/role extraction  
→ Application matching  
→ User approval  
→ PostgreSQL update

After the initial Gmail import, OfferBase uses Gmail history IDs to process only newly received messages.

## Tech Stack

### Frontend
- React
- TypeScript
- Tailwind CSS
- Vite

### Backend
- Java
- Spring Boot
- Spring Security
- JPA / Hibernate
- Maven

### Machine Learning
- Python
- scikit-learn
- pandas
- FastAPI
- TF-IDF
- Logistic Regression

### Data & APIs
- PostgreSQL
- Gmail API
- OAuth 2.0
- REST APIs
- JWT

## Testing

The backend includes tests covering:

- Email classification
- Recruiting-email filtering
- Company and role extraction
- Application matching
- Email classification pipeline
- Spring application context

## Running Locally

### 1. PostgreSQL

Create a PostgreSQL database named:

```text
offerbase

