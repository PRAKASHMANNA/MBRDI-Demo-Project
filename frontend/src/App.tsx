import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import UsersPage from './pages/UsersPage'
import ProductsPage from './pages/ProductsPage'
import OrdersPage from './pages/OrdersPage'
import InventoryPage from './pages/InventoryPage'
import NotificationsPage from './pages/NotificationsPage'

const App = () => {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/products" replace />} />

          <Route path="/products" element={<ProductsPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/inventory" element={<InventoryPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/users" element={<UsersPage />} />

          <Route path="*" element={<Navigate to="/products" replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  )
}

export default App