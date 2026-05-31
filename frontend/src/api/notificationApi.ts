import api from './axios'
import type { ApiResponse, Notification } from '../types'

export const getAllNotifications = () =>
  api.get<ApiResponse<Notification[]>>('/api/v1/notifications')

export const getNotificationsByUser = (userId: number) =>
  api.get<ApiResponse<Notification[]>>(`/api/v1/notifications/user/${userId}`)

export const getNotificationsByOrder = (orderId: number) =>
  api.get<ApiResponse<Notification[]>>(`/api/v1/notifications/order/${orderId}`)