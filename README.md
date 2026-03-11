# Global Artisan Marketplace

A microservices-based e-commerce platform for handmade crafts (jewelry, textiles, etc.) built for **CTSE Assignment (SE4010) – Cloud Computing**.

## Architecture

| Component | Port | Responsibility |
|-----------|------|----------------|
| **API Gateway** | 8084 | Single entry point, routes to all services |
| **User Service** | 8080 | Auth, profiles (buyer/seller) |
| **Listing Service** | 8081 | Product catalog, search |
| **Order Service** | 8082 | Cart, checkout, mock payment |
| **Review Service** | 8083 | Post-purchase reviews |