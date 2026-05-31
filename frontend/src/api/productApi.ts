import api from './axios'
import type { ApiResponse, Product, ProductRequest } from '../types'

export const getProducts = () =>
  api.get<ApiResponse<Product[]>>('/api/v1/products')

export const getProductById = (id: number) =>
  api.get<ApiResponse<Product>>(`/api/v1/products/${id}`)

export const createProduct = (data: ProductRequest) =>
  api.post<ApiResponse<Product>>('/api/v1/products', data)

export const updateProduct = (id: number, data: ProductRequest) =>
  api.put<ApiResponse<Product>>(`/api/v1/products/${id}`, data)

export const deleteProduct = (id: number) =>
  api.delete<ApiResponse<string>>(`/api/v1/products/${id}`)

export const searchProducts = (name: string) =>
  api.get<ApiResponse<Product[]>>(
    `/api/v1/products/search?name=${encodeURIComponent(name)}`
  )