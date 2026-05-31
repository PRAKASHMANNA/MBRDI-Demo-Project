import { useState, useEffect } from 'react'
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Form,
  Modal,
  Row,
  Spinner
} from 'react-bootstrap'
import type { User, UserRequest } from '../types'
import { getUsers, createUser, deleteUser } from '../api/userApi'

const formatDate = (value?: string) => {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

const getInitials = (name: string) => {
  return name
    .split(' ')
    .map(part => part.charAt(0))
    .join('')
    .slice(0, 2)
    .toUpperCase()
}

const UsersPage = () => {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showModal, setShowModal] = useState(false)

  const [form, setForm] = useState<UserRequest>({
    name: '',
    email: '',
    password: '',
    phone: ''
  })

  const fetchUsers = async () => {
    setLoading(true)
    setError('')

    try {
      const res = await getUsers()
      setUsers(res.data.data || [])
    } catch {
      setError('Failed to fetch users. Check user-service and API Gateway.')
      setUsers([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchUsers()
  }, [])

  const handleCreate = async () => {
    if (!form.name.trim() || !form.email.trim() || !form.password.trim()) {
      setError('Please enter name, email and password.')
      return
    }

    try {
      await createUser(form)
      setSuccess('User created successfully')
      setError('')
      setShowModal(false)
      setForm({ name: '', email: '', password: '', phone: '' })
      fetchUsers()
    } catch {
      setError('Failed to create user')
    }
  }

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this user?')) return

    try {
      await deleteUser(id)
      setSuccess('User deleted successfully')
      setError('')
      fetchUsers()
    } catch {
      setError('Failed to delete user')
    }
  }

  return (
    <div className="page-container crm-page">
      <Row className="g-4">
        <Col lg={4}>
          <div className="crm-sidebar">
            <Badge bg="light" text="dark">Customer CRM</Badge>

            <h1>Mercedes Customers</h1>

            <p>
              Manage customer profiles, contact details and user accounts for order placement.
            </p>

            <div className="crm-big-number">
              <span>Total Customers</span>
              <strong>{users.length}</strong>
            </div>

            <Button variant="light" className="w-100" onClick={() => setShowModal(true)}>
              + Add Customer
            </Button>
          </div>
        </Col>

        <Col lg={8}>
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
          ) : users.length === 0 ? (
            <Alert variant="secondary">No users found</Alert>
          ) : (
            <Row className="g-4">
              {users.map(user => (
                <Col md={6} key={user.id}>
                  <Card className="customer-card h-100">
                    <Card.Body>
                      <div className="customer-top">
                        <div className="customer-avatar">
                          {getInitials(user.name || 'User')}
                        </div>

                        <Badge bg="success">Active</Badge>
                      </div>

                      <h3>{user.name}</h3>

                      <div className="customer-info">
                        <div>
                          <span>Email</span>
                          <strong>{user.email}</strong>
                        </div>

                        <div>
                          <span>Phone</span>
                          <strong>{user.phone || '-'}</strong>
                        </div>

                        <div>
                          <span>Created At</span>
                          <strong>{formatDate(user.createdAt)}</strong>
                        </div>
                      </div>

                      <Button
                        variant="outline-danger"
                        size="sm"
                        className="w-100 mt-4"
                        onClick={() => handleDelete(user.id)}
                      >
                        Delete Customer
                      </Button>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </Row>
          )}
        </Col>
      </Row>

      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Add New Customer</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control
                value={form.name}
                onChange={e => setForm({ ...form, name: e.target.value })}
                placeholder="John Doe"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                placeholder="john@mercedesbenz.com"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })}
                placeholder="Enter password"
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Phone</Form.Label>
              <Form.Control
                value={form.phone}
                onChange={e => setForm({ ...form, phone: e.target.value })}
                placeholder="9876543210"
              />
            </Form.Group>
          </Form>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Cancel
          </Button>

          <Button variant="dark" onClick={handleCreate}>
            Create Customer
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  )
}

export default UsersPage