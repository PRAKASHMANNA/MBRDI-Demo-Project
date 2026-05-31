import api from './axios'
import type { ApiResponse, User, UserRequest } from '../types'

export const getUsers = () =>
  api.get<ApiResponse<User[]>>('/api/v1/users')

export const getUserById = (id: number) =>
  api.get<ApiResponse<User>>(`/api/v1/users/${id}`)

export const createUser = (data: UserRequest) =>
  api.post<ApiResponse<User>>('/api/v1/users', data)

export const updateUser = (id: number, data: UserRequest) =>
  api.put<ApiResponse<User>>(`/api/v1/users/${id}`, data)

export const deleteUser = (id: number) =>
  api.delete<ApiResponse<string>>(`/api/v1/users/${id}`)