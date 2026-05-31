import api from './axios'
import type { ApiResponse, Order, OrderRequest } from '../types'

export const getOrders = () =>
  api.get<ApiResponse<Order[]>>('/api/v1/orders')

export const getOrderById = (id: number) =>
  api.get<ApiResponse<Order>>(`/api/v1/orders/${id}`)

export const getOrdersByUser = (userId: number) =>
  api.get<ApiResponse<Order[]>>(`/api/v1/orders/user/${userId}`)

export const placeOrder = (data: OrderRequest) =>
  api.post<ApiResponse<Order>>('/api/v1/orders', data)

export const cancelOrder = (id: number) =>
  api.patch<ApiResponse<Order>>(`/api/v1/orders/${id}/cancel`)