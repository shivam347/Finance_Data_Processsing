# Frontend Architecture

This document explains the architecture of the React frontend for the Finance Dashboard.

## Architecture Pattern
The application uses a standard Single Page Application (SPA) architecture built with **React**, **Vite**, and styled with **Tailwind CSS**.

### 1. File Structure
The `src/` directory is organized by feature and function:
- **`components/`**: Reusable UI elements (Buttons, Cards, Modals, App Layout, Sidebar).
- **`pages/`**: View-level components mapping directly to application routes (Dashboard, Records, Login).
- **`context/`**: React Context providers (specifically `AuthContext` for managing the logged-in user state and decoding the JWT token).
- **`services/`**: API communication. `api.js` contains a centralized Axios instance configured to inject the authentication token into every outgoing request.
- **`utils/`**: Helper functions like date rendering and currency formatters.

### 2. State Management
- **Local UI State**: Managed via standard `useState` / `useReducer` hooks.
- **Form State**: Managed using `react-hook-form` for efficient uncontrolled component rendering and validation.
- **Global Auth State**: Managed using the Context API (`AuthContext`), storing the authenticated user object, their extracted role, and handling clear/set operations on `localStorage`.

### 3. Routing & Security Strategy (RBAC)
We utilize `react-router-dom` with a `ProtectedRoute` wrapper component. The routing strictly enforces the Role-Based Access Control logic:

*   **Public Routes**: `/login`, `/register`. Automatically redirects to `/dashboard` if already authenticated.
*   **Protected Core Routes (`/dashboard`, `/records`)**:
    *   **VIEWER**: The UI restricts their view to only their records by fetching from appropriately scope-limited API endpoints. Creation/Editing options are removed from the DOM.
    *   **ANALYST**: Allowed to view all aggregation charts and records but unable to mutate data.
*   **Admin Routes (`/users`)**: Wrapped in a check that ensures `user.role === 'ADMIN'`.

## Network Interception
To securely communicate with the Spring Boot backend, the `services/api.js` configures an Axios interceptor. Before any request leaves the browser:
1. It reaches out to `localStorage` to grab the current token.
2. Appends `Authorization: Bearer <token>` to the HTTP Headers.
3. Automatically un-authenticates the user if the server responds with a `401 Unauthorized` token expiry.

## Charts
Data visualization is handled by **Recharts**, seamlessly mapping the complex summary aggregations (like category grouping) into interactive Pie and Bar charts for Analysts and Admins.
