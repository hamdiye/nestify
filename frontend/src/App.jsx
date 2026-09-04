import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { UserProvider, useUser } from './context/UserContext';
import Sidebar from './components/Sidebar';
import SelectUserPage from './pages/SelectUserPage';
import DashboardPage from './pages/DashboardPage';
import HousesPage from './pages/HousesPage';
import HouseDetailPage from './pages/HouseDetailPage';
import ProfilePage from './pages/ProfilePage';

function PrivateLayout({ children }) {
  const { currentUser } = useUser();
  if (!currentUser) return <Navigate to="/" replace />;
  return (
    <div className="app-layout">
      <Sidebar />
      <main className="main-content">{children}</main>
    </div>
  );
}

function AppRoutes() {
  const { currentUser } = useUser();
  return (
    <Routes>
      <Route path="/" element={currentUser ? <Navigate to="/dashboard" replace /> : <SelectUserPage />} />
      <Route path="/dashboard"  element={<PrivateLayout><DashboardPage /></PrivateLayout>} />
      <Route path="/houses"     element={<PrivateLayout><HousesPage /></PrivateLayout>} />
      <Route path="/houses/:id" element={<PrivateLayout><HouseDetailPage /></PrivateLayout>} />
      <Route path="/profile"    element={<PrivateLayout><ProfilePage /></PrivateLayout>} />
      <Route path="*"          element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <UserProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </UserProvider>
  );
}
