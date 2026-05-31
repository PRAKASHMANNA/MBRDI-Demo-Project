import { useState, useEffect } from 'react'
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Form,
  InputGroup,
  Row,
  Spinner
} from 'react-bootstrap'
import type { Notification } from '../types'
import {
  getAllNotifications,
  getNotificationsByUser,
  getNotificationsByOrder
} from '../api/notificationApi'

const statusColor: Record<string, string> = {
  SENT: 'success',
  FAILED: 'danger',
  PENDING: 'warning'
}

const formatDateTime = (value?: string) => {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [filterType, setFilterType] = useState<'all' | 'user' | 'order'>('all')
  const [filterId, setFilterId] = useState('')

  const fetchAll = async () => {
    setLoading(true)
    setError('')

    try {
      const res = await getAllNotifications()
      setNotifications(res.data.data || [])
    } catch {
      setError('Failed to fetch notifications. Check notification-service and API Gateway.')
      setNotifications([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAll()
  }, [])

  const handleFilter = async () => {
    if (filterType === 'all') {
      setFilterId('')
      fetchAll()
      return
    }

    if (!filterId.trim()) {
      setError('Please enter an ID')
      return
    }

    setLoading(true)
    setError('')

    try {
      const res =
        filterType === 'user'
          ? await getNotificationsByUser(Number(filterId))
          : await getNotificationsByOrder(Number(filterId))

      setNotifications(res.data.data || [])
    } catch {
      setError('No notifications found')
      setNotifications([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page-container kafka-page">
      <div className="kafka-hero">
        <div>
          <Badge bg="light" text="dark">Kafka Event Stream</Badge>
          <h1>Notification Center</h1>
          <p>
            View order events consumed from Kafka and processed by notification-service.
          </p>
        </div>

        <Button variant="light" onClick={fetchAll}>
          Refresh Stream
        </Button>
      </div>

      <Card className="event-filter-card">
        <Card.Body>
          <InputGroup>
            <Form.Select
              value={filterType}
              onChange={e => {
                const value = e.target.value as 'all' | 'user' | 'order'
                setFilterType(value)

                if (value === 'all') {
                  setFilterId('')
                }
              }}
            >
              <option value="all">All Events</option>
              <option value="user">Filter by User ID</option>
              <option value="order">Filter by Order ID</option>
            </Form.Select>

            <Form.Control
              placeholder="Enter ID..."
              value={filterId}
              disabled={filterType === 'all'}
              onChange={e => setFilterId(e.target.value)}
            />

            <Button variant="dark" onClick={handleFilter}>
              Apply
            </Button>
          </InputGroup>
        </Card.Body>
      </Card>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {loading ? (
        <div className="text-center mt-5">
          <Spinner animation="border" variant="light" />
        </div>
      ) : notifications.length === 0 ? (
        <Alert variant="secondary">No notifications found</Alert>
      ) : (
        <Row className="g-4">
          <Col lg={3}>
            <div className="kafka-side-stats">
              <div>
                <span>Total Events</span>
                <strong>{notifications.length}</strong>
              </div>

              <div>
                <span>Sent</span>
                <strong>
                  {notifications.filter(item => item.status === 'SENT').length}
                </strong>
              </div>

              <div>
                <span>Failed</span>
                <strong>
                  {notifications.filter(item => item.status === 'FAILED').length}
                </strong>
              </div>
            </div>
          </Col>

          <Col lg={9}>
            <div className="event-stream">
              {notifications.map(notification => (
                <div className="event-item" key={notification.id}>
                  <div className="event-dot" />

                  <Card className="event-card">
                    <Card.Body>
                      <div className="event-card-top">
                        <div>
                          <Badge bg="info" text="dark">
                            {notification.type}
                          </Badge>

                          <h3>Order #{notification.orderId}</h3>
                        </div>

                        <Badge bg={statusColor[notification.status] || 'secondary'}>
                          {notification.status}
                        </Badge>
                      </div>

                      <p>{notification.message}</p>

                      <div className="event-meta">
                        <span>User ID: {notification.userId}</span>
                        <span>{formatDateTime(notification.createdAt)}</span>
                      </div>
                    </Card.Body>
                  </Card>
                </div>
              ))}
            </div>
          </Col>
        </Row>
      )}
    </div>
  )
}

export default NotificationsPage