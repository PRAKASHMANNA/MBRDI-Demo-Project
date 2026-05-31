import { useState, useEffect } from 'react'
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Form,
  Modal,
  ProgressBar,
  Row,
  Spinner
} from 'react-bootstrap'
import api from '../api/axios'
import type { Order, OrderRequest } from '../types'

const statusColor: Record<string, string> = {
  PENDING: 'warning',
  PLACED: 'info',
  CONFIRMED: 'info',
  SHIPPED: 'primary',
  DELIVERED: 'success',
  CANCELLED: 'danger'
}

const statusProgress: Record<string, number> = {
  PENDING: 20,
  PLACED: 25,
  CONFIRMED: 45,
  SHIPPED: 75,
  DELIVERED: 100,
  CANCELLED: 100
}

const formatDate = (value?: string) => {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

const OrdersPage = () => {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showModal, setShowModal] = useState(false)

  const [form, setForm] = useState<OrderRequest>({
    userId: 1,
    productId: 1,
    quantity: 1
  })

  const fetchOrders = async () => {
    setLoading(true)
    setError('')

    try {
      const res = await api.get('/api/v1/orders')
      console.log('Orders response:', res.data)
      setOrders(res.data.data || [])
    } catch (err: any) {
      console.error('Order fetch failed:', err)

      setError(
        `Failed to fetch orders. Status: ${err.response?.status || 'NO_STATUS'} - ${
          err.response?.data?.message ||
          err.response?.data?.error ||
          err.message
        }`
      )

      setOrders([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  const handlePlaceOrder = async () => {
    if (form.userId <= 0 || form.productId <= 0 || form.quantity <= 0) {
      setError('Please enter valid user id, product id and quantity.')
      return
    }

    try {
      await api.post('/api/v1/orders', form)

      setSuccess('Order placed successfully!')
      setError('')
      setShowModal(false)

      setForm({
        userId: 1,
        productId: 1,
        quantity: 1
      })

      fetchOrders()
    } catch (err: any) {
      console.error('Order create failed:', err)

      setError(
        `Failed to place order. Status: ${err.response?.status || 'NO_STATUS'} - ${
          err.response?.data?.message ||
          err.response?.data?.error ||
          err.message
        }`
      )
    }
  }

  const handleCancel = async (id: number) => {
    if (!window.confirm('Cancel this order?')) return

    try {
      await api.patch(`/api/v1/orders/${id}/cancel`)

      setSuccess('Order cancelled successfully')
      setError('')
      fetchOrders()
    } catch (err: any) {
      console.error('Order cancel failed:', err)

      setError(
        `Failed to cancel order. Status: ${err.response?.status || 'NO_STATUS'} - ${
          err.response?.data?.message ||
          err.response?.data?.error ||
          err.message
        }`
      )
    }
  }

  const totalRevenue = orders
    .filter(order => order.status !== 'CANCELLED')
    .reduce((sum, order) => sum + Number(order.totalPrice || 0), 0)

  const activeOrders = orders.filter(
    order => order.status !== 'CANCELLED' && order.status !== 'DELIVERED'
  ).length

  return (
    <div className="page-container orders-tracker-page">
      <div className="orders-top-panel">
        <div>
          <Badge bg="light" text="dark">
            Order Tracking
          </Badge>

          <h1>Mercedes Order Control Tower</h1>

          <p>
            Monitor live customer orders, order value, status progress and
            cancellation flow.
          </p>
        </div>

        <Button variant="light" onClick={() => setShowModal(true)}>
          + Place Order
        </Button>
      </div>

      <Row className="g-4 mb-4">
        <Col lg={4}>
          <Card className="order-summary-card">
            <Card.Body>
              <span>Total Orders</span>
              <h2>{orders.length}</h2>
              <p>All orders created from ecommerce platform.</p>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card className="order-summary-card active">
            <Card.Body>
              <span>Active Orders</span>
              <h2>{activeOrders}</h2>
              <p>Orders not delivered or cancelled yet.</p>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card className="order-summary-card revenue">
            <Card.Body>
              <span>Total Revenue</span>
              <h2>₹{totalRevenue.toLocaleString('en-IN')}</h2>
              <p>Calculated excluding cancelled orders.</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess('')}>
          {success}
        </Alert>
      )}

      {loading ? (
        <div className="text-center mt-5">
          <Spinner animation="border" variant="light" />
        </div>
      ) : orders.length === 0 ? (
        <Alert variant="secondary">No orders found</Alert>
      ) : (
        <div className="order-timeline-wrapper">
          {orders.map(order => (
            <Card className="order-tracking-card" key={order.id}>
              <Card.Body>
                <div className="order-card-header">
                  <div>
                    <Badge bg="secondary">Order #{order.id}</Badge>

                    <h3>{order.productName || 'Mercedes Product'}</h3>

                    <p>
                      User ID: {order.userId} • Product ID: {order.productId}
                    </p>
                  </div>

                  <Badge
                    bg={statusColor[order.status] || 'secondary'}
                    className="status-badge"
                  >
                    {order.status || 'UNKNOWN'}
                  </Badge>
                </div>

                <div className="order-card-grid">
                  <div>
                    <span>Quantity</span>
                    <strong>{order.quantity}</strong>
                  </div>

                  <div>
                    <span>Total Price</span>
                    <strong>
                      ₹{Number(order.totalPrice || 0).toLocaleString('en-IN')}
                    </strong>
                  </div>

                  <div>
                    <span>Created</span>
                    <strong>{formatDate(order.createdAt)}</strong>
                  </div>
                </div>

                <div className="order-progress-area">
                  <div className="d-flex justify-content-between mb-2">
                    <small>Order Progress</small>
                    <small>{statusProgress[order.status] || 10}%</small>
                  </div>

                  <ProgressBar
                    now={statusProgress[order.status] || 10}
                    variant={statusColor[order.status] || 'secondary'}
                  />
                </div>

                <div className="order-actions">
                  {order.status !== 'CANCELLED' &&
                  order.status !== 'DELIVERED' ? (
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleCancel(order.id)}
                    >
                      Cancel Order
                    </Button>
                  ) : (
                    <span className="closed-order-text">
                      No action available
                    </span>
                  )}
                </div>
              </Card.Body>
            </Card>
          ))}
        </div>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Place New Order</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>User ID</Form.Label>
              <Form.Control
                type="number"
                min={1}
                value={form.userId}
                onChange={e =>
                  setForm({ ...form, userId: Number(e.target.value) })
                }
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Product ID</Form.Label>
              <Form.Control
                type="number"
                min={1}
                value={form.productId}
                onChange={e =>
                  setForm({ ...form, productId: Number(e.target.value) })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Quantity</Form.Label>
              <Form.Control
                type="number"
                min={1}
                value={form.quantity}
                onChange={e =>
                  setForm({ ...form, quantity: Number(e.target.value) })
                }
              />
            </Form.Group>
          </Form>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancel
          </Button>

          <Button variant="dark" onClick={handlePlaceOrder}>
            Place Order
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  )
}

export default OrdersPage