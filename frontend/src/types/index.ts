export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T | null
  error: string | null
}

export interface User {
  id: number
  name: string
  email: string
  phone: string | null
  createdAt?: string
}

export interface UserRequest {
  name: string
  email: string
  password: string
  phone?: string
}

export interface Product {
  id: number
  name: string
  description: string | null
  price: number
  category: string
  stock: number
  createdAt?: string
}

export interface ProductRequest {
  name: string
  description?: string
  price: number
  category: string
  stock: number
}

export interface Order {
  id: number
  userId: number
  productId: number
  productName: string
  quantity: number
  totalPrice: number
  status: string
  createdAt?: string
}

export interface OrderRequest {
  userId: number
  productId: number
  quantity: number
}

export interface Inventory {
  id: number
  productId: number
  productName: string
  availableStock: number
  reservedStock: number
  updatedAt?: string
}

export interface InventoryRequest {
  productId: number
  productName: string
  availableStock: number
}

export interface Notification {
  id: number
  orderId: number
  userId: number
  message: string
  type: string
  status: string
  createdAt?: string
}