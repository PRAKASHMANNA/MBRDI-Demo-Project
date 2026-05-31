import api from './axios'
import type { ApiResponse, Inventory, InventoryRequest } from '../types'

export const getAllInventory = () =>
  api.get<ApiResponse<Inventory[]>>('/api/v1/inventory')

export const getInventoryByProduct = (productId: number) =>
  api.get<ApiResponse<Inventory>>(`/api/v1/inventory/product/${productId}`)

export const addInventory = (data: InventoryRequest) =>
  api.post<ApiResponse<Inventory>>('/api/v1/inventory', data)

export const updateStock = (productId: number, quantity: number) =>
  api.patch<ApiResponse<Inventory>>(
    `/api/v1/inventory/product/${productId}/stock?quantity=${quantity}`
  )